package cat.psychward.dbus

import cat.psychward.dbus.api.PlayerType
import cat.psychward.dbus.api.data.MediaListenerType
import cat.psychward.dbus.api.data.Timestamp
import cat.psychward.dbus.api.data.TrackMetadata
import cat.psychward.dbus.api.data.fromSeconds
import cat.psychward.dbus.api.player.lyrics.LyricsEngine
import cat.psychward.dbus.api.player.lyrics.TrackLyrics
import org.freedesktop.dbus.interfaces.DBus
import kotlin.system.exitProcess
import kotlin.test.Test

class DBusInitializerTest {

    @Test
    fun playerTest() {
        val dbus = DBusInitializer()
        val type: PlayerType = dbus.determinePlayer()
        val source = dbus.getSource(type)
        val playerBusName = source.getPlayer()

        var currentTrack: TrackMetadata = dbus.getCurrentTrack(source).also {
            it.lyrics = TrackLyrics(it)
        }
        println(currentTrack.toString())

        dbus.listenToPlayerChanges(playerBusName, MediaListenerType.CHANGED) {
            println("Changed")
            currentTrack = dbus.getCurrentTrack(source).also {
                it.lyrics = TrackLyrics(it)
            }
        }
        dbus.listenToPlayerChanges(playerBusName, MediaListenerType.PAUSE) {
            println("Paused")
        }
        dbus.listenToPlayerChanges(playerBusName, MediaListenerType.RESUME) {
            println("Resumed")
//            currentTrack = dbus.getCurrentTrack(source).also {
//                it.lyrics = TrackLyrics(it)
//            }
        }
        dbus.listenToPlayerChanges(playerBusName, MediaListenerType.STOP) {
            println("Stopped")
        }

        val engine = LyricsEngine()
        engine.start(
            getTrack = { currentTrack },
            getPosition = { dbus.position(playerBusName) }
        ) { lyric ->
            println("${currentTrack.formatProgress()}: $lyric")
        }

        while (true) {
            val pos = dbus.position(playerBusName)
            currentTrack.setProgressFromPlayer(pos)
            Thread.sleep(1000)
        }
    }

}