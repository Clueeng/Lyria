package cat.psychward.dbus

import cat.psychward.dbus.api.DBusMediaPlayer
import cat.psychward.dbus.api.data.TrackInfo
import cat.psychward.dbus.api.data.TrackMetadata
import cat.psychward.dbus.api.source.ISource
import cat.psychward.dbus.api.source.impl.SpotifySource
import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder
import org.freedesktop.dbus.interfaces.DBus
import org.freedesktop.dbus.interfaces.Properties
import org.freedesktop.dbus.types.Variant
import kotlin.jvm.java

@Suppress("UNCHECKED_CAST")
class DBusInitializer(val mprisPath: String = "org.mpris.MediaPlayer2") {
    val dbus : DBusConnection = DBusConnectionBuilder
        .forSessionBus()
        .receivingThreadConfig()
        .withSignalThreadCount(4)
        .withMethodCallThreadCount(2)
        .connectionConfig()
        .withShared(true)
    .build()
    val mediaPlayer = DBusMediaPlayer()

    fun printMedia() {
        val bus = DBusConnectionBuilder.forSessionBus().build()
        val dbus = bus.getRemoteObject(
            "org.freedesktop.DBus",
            "/org/freedesktop/DBus",
            DBus::class.java
        )

        val names = dbus.ListNames()

        names.filter { it.startsWith(mprisPath) }
            .forEach(::println)
    }


    fun fromSpotify() : TrackMetadata {
        val bus = DBusConnectionBuilder.forSessionBus().build()

        val source: ISource = SpotifySource()
        val playerName = source.getPlayer()

        val properties = bus.getRemoteObject(
            playerName,
            "/org/mpris/MediaPlayer2",
            Properties::class.java
        )

        val metadata = properties.Get<Map<String, Any>>(
            "$mprisPath.Player",
            "Metadata"
        )
        val trackName : String = metadata[TrackInfo.TRACK_NAME.value] as String
        val trackArtists : List<String> = metadata[TrackInfo.ARTIST_NAME.value] as List<String>
        val albumName : String = metadata[TrackInfo.ALBUM_NAME.value] as String
        val albumArtists : List<String> = metadata[TrackInfo.ALBUM_ARTIST.value] as List<String>
        val trackArt : String = metadata[TrackInfo.ART_URL.value] as String

        return TrackMetadata(trackName, trackArtists, albumName,
            albumArtists, trackArt)
    }
}