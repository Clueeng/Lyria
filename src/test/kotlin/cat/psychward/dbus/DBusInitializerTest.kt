package cat.psychward.dbus

import cat.psychward.dbus.api.PlayerType
import cat.psychward.dbus.api.data.Timestamp
import cat.psychward.dbus.api.data.fromSeconds
import cat.psychward.dbus.api.player.lyrics.TrackLyrics
import kotlin.system.exitProcess
import kotlin.test.Test

class DBusInitializerTest {

    @Test
    fun playerTest() {
        val dbus = DBusInitializer()
        val type: PlayerType = dbus.determinePlayer()
        val currentTrack = dbus.getCurrentTrack(type)
        println(currentTrack.toString())

        val lyrics = TrackLyrics(currentTrack)
        val atSomewhere = lyrics.getCurrentLyric(fromSeconds(currentTrack.trackProgress))
        println("Lyrics at ${currentTrack.trackProgress}: $atSomewhere")

        val plain = lyrics.plainLyrics
        println("Plain lyrics: $plain")
    }

}