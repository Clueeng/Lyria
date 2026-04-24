package cat.psychward.dbus

import cat.psychward.dbus.api.PlayerType
import cat.psychward.dbus.api.data.MediaListenerType
import cat.psychward.dbus.api.data.TrackMetadata
import cat.psychward.dbus.api.player.lyrics.LyricSession
import org.freedesktop.dbus.errors.NoReply
import org.freedesktop.dbus.errors.ServiceUnknown
import kotlin.test.Test

class DBusInitializerTest {

    private lateinit var dbus: DBusInitializer

    private lateinit var currentPlayerType: PlayerType
    private lateinit var currentTrack: TrackMetadata
    private lateinit var playerBusName: String

    private lateinit var lyricSession: LyricSession

    @Test
    fun playerTest() {
        dbus = DBusInitializer()

        connectToPlayer()
        startLyrics()
        registerListeners()

        printLoop()
    }

    private fun connectToPlayer() {
        currentPlayerType = dbus.determinePlayer()

        if (currentPlayerType == PlayerType.None) {
            error("No media player detected")
        }

        val source = dbus.getSource(currentPlayerType)

        playerBusName = source.getPlayer()
        currentTrack = dbus.getCurrentTrack(source)

        println("Connected to: $playerBusName")
        println("Track: ${currentTrack.name}")
        println("Artist: ${currentTrack.artists.joinToString()}")
    }

    private fun startLyrics() {
        lyricSession = LyricSession(
            positionProvider = {
                dbus.position(playerBusName)
            },
            beforeCount = 2,
            afterCount = 1
        )

        lyricSession.load(currentTrack)
        lyricSession.start()
    }

    private fun registerListeners() {
        dbus.listenToPlayerChanges(
            playerBusName,
            MediaListenerType.CHANGED
        ) {
            onTrackChanged()
        }

        dbus.listenToPlayerChanges(
            playerBusName,
            MediaListenerType.PAUSE
        ) {
            println("Playback paused")
        }

        dbus.listenToPlayerChanges(
            playerBusName,
            MediaListenerType.RESUME
        ) {
            println("Playback resumed")
        }

        dbus.listenToPlayerChanges(
            playerBusName,
            MediaListenerType.STOP
        ) {
            println("Playback stopped")
        }
    }

    private fun onTrackChanged() {
        try {
            currentPlayerType = dbus.determinePlayer()
            val source = dbus.getSource(currentPlayerType)

            currentTrack = dbus.getCurrentTrack(source)

            println("\nTrack changed -> ${currentTrack.name}")

            lyricSession.load(currentTrack)

        } catch (e: Exception) {
            println("Failed to reload track: ${e.message}")
        }
    }

    private fun printLoop() {
        while (true) {
            try {
                val state = lyricSession.getRenderState()

                if(!state.surrounding.isEmpty()) {
                    println("\n======= Lyrics =======")
                    state.surrounding.forEach { line ->
                        if (line == state.current) {
                            println(">> ${line.text}")
                        } else {
                            println("   ${line.text}")
                        }
                    }
                    println("======================")

                }
                Thread.sleep(1000)

            } catch (e: NoReply) {
                reconnect()

            } catch (e: ServiceUnknown) {
                reconnect()
            }
        }
    }

    private fun reconnect() {
        println("Player disconnected. Reconnecting...")

        try {
            connectToPlayer()

            lyricSession.stop()
            startLyrics()
            registerListeners()

            println("Reconnected successfully")

        } catch (e: Exception) {
            println("Reconnect failed: ${e.message}")
        }
    }
}