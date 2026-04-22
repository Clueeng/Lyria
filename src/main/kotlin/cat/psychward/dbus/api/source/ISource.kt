package cat.psychward.dbus.api.source

import cat.psychward.dbus.api.data.TrackMetadata
import org.freedesktop.dbus.interfaces.Properties

interface ISource {
    fun getPlayer(): String
    fun getName(): String
    fun extractMetadata(properties: Properties): TrackMetadata
}