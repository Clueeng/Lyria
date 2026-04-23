package cat.psychward.dbus

import cat.psychward.dbus.api.PlayerType
import cat.psychward.dbus.api.data.MediaListenerType
import cat.psychward.dbus.api.data.Timestamp
import cat.psychward.dbus.api.data.TrackMetadata
import cat.psychward.dbus.api.data.fromSeconds
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

        var currentTrack: TrackMetadata = dbus.getCurrentTrack(source)
        println(currentTrack.toString())

        dbus.listenToPlayerChanges(playerBusName, MediaListenerType.CHANGED) {
            println("Changed")
            currentTrack = dbus.getCurrentTrack(source)
        }
        dbus.listenToPlayerChanges(playerBusName, MediaListenerType.PAUSE) {
            println("Paused")
        }
        dbus.listenToPlayerChanges(playerBusName, MediaListenerType.RESUME) {
            println("Resumed")
        }
        dbus.listenToPlayerChanges(playerBusName, MediaListenerType.STOP) {
            println("Stopped")
        }
        currentTrack.listenLyricsChanges() { lyric ->
            println("${currentTrack.formatProgress()}: $lyric")
        }

        println("PROGRESS : ${currentTrack.formatProgress()}")
        while (true) {
            currentTrack.setProgressFromPlayer(dbus.position(playerBusName))
            Thread.sleep(1000)
        }
    }

}