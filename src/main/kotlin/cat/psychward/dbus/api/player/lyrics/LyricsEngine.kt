package cat.psychward.dbus.api.player.lyrics

import cat.psychward.dbus.api.data.TrackMetadata
import cat.psychward.dbus.api.data.fromSeconds

class LyricsEngine {

    private var lastLyric: String? = null

    fun start(
        getTrack: () -> TrackMetadata,
        getPosition: () -> Double,
        onChange: (String) -> Unit
    ) {
        Thread {
            while (true) {

                val track = getTrack()
                val lyrics = track.lyrics

                val current = lyrics?.getCurrentLyric(fromSeconds(getPosition()))

                if (current != lastLyric) {
                    lastLyric = current
                    if (current != null) {
                        onChange(current)
                    }
                }

                Thread.sleep(100)
            }
        }.start()
    }
}