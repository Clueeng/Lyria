package cat.psychward.dbus

import cat.psychward.dbus.api.PlayerType
import kotlin.system.exitProcess
import kotlin.test.Test

class DBusInitializerTest {

    @Test
    fun before() {
        println("Testing DBus")
        val dbus = DBusInitializer()
        val type: PlayerType = dbus.determinePlayer()
        val currentTrack = dbus.getCurrentTrack(type)
        println(currentTrack.toString())
    }

}