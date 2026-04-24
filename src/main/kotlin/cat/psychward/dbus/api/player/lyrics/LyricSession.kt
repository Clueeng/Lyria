package cat.psychward.dbus.api.player.lyrics

import cat.psychward.dbus.api.data.LyricLine
import cat.psychward.dbus.api.data.LyricRenderState
import cat.psychward.dbus.api.data.LyricsSnapshot
import cat.psychward.dbus.api.data.TrackMetadata
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LyricSession(
    private val positionProvider: () -> Double,
    private val beforeCount: Int = 2,
    private val afterCount: Int = 1
) {
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val loaderExecutor = Executors.newSingleThreadExecutor()

    @Volatile
    private var lyrics: LyricsSnapshot? = null

    @Volatile
    private var lastLine: LyricLine? = null

    @Volatile
    private var renderState = LyricRenderState(
        current = null,
        surrounding = emptyList()
    )

    fun load(track: TrackMetadata) {
        loaderExecutor.submit {
            val snapshot = LyricsService.fetch(track)

            lyrics = snapshot
            lastLine = null

            renderState = LyricRenderState(
                current = null,
                surrounding = emptyList()
            )
        }
    }

    fun start() {
        executor.scheduleAtFixedRate({
            val snapshot = lyrics ?: return@scheduleAtFixedRate

            val position = positionProvider()
            val current = snapshot.currentAt(position)

            if (current != lastLine) {
                lastLine = current

                renderState = LyricRenderState(
                    current = current,
                    surrounding = snapshot.windowAt(
                        seconds = position,
                        before = beforeCount,
                        after = afterCount
                    )
                )
            }

        }, 0, 100, TimeUnit.MILLISECONDS)
    }

    fun getRenderState(): LyricRenderState {
        return renderState
    }

    fun stop() {
        executor.shutdownNow()
        loaderExecutor.shutdownNow()
    }
}