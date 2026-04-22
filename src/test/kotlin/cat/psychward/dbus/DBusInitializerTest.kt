package cat.psychward.dbus

import cat.psychward.dbus.api.PlayerType
import kotlin.system.exitProcess
import kotlin.test.Test

class DBusInitializerTest {

    @Test
    fun before() {
        println("Testing DBus")
        val dbus = DBusInitializer()
        val type: PlayerType = if(dbus.isRunning("spotify"))
            PlayerType.Spotify
        else if(dbus.isRunning("firefox"))
            PlayerType.Browser
        else {
            println("No music playing")
            exitProcess(0)
        }

        val currentTrack = dbus.getCurrentTrack(type)
        println(currentTrack.toString())
    }

}