package cat.psychward.dbus.api

import org.freedesktop.dbus.annotations.DBusBoundProperty
import org.freedesktop.dbus.annotations.DBusInterfaceName
import org.freedesktop.dbus.interfaces.DBusInterface

@DBusInterfaceName("org.mpris.MediaPlayer2")
interface MediaPlayer : DBusInterface {
    fun raise()
    fun quit()


    @DBusBoundProperty
    fun getHasTrackList(): Boolean
}