package cat.psychward.dbus

import kotlin.test.Test

class DBusInitializerTest {

    @Test
    fun before() {
        println("Testing DBus")
        val dbus = DBusInitializer()
        val spotify = dbus.fromSpotify()
        println(spotify.toString())
    }

}