package cat.psychward.dbus.api.player.source.impl

import cat.psychward.dbus.api.data.TrackMetadata
import cat.psychward.dbus.api.player.source.ISource
import org.freedesktop.dbus.interfaces.Properties
import org.freedesktop.dbus.types.UInt64

@Suppress("UNCHECKED_CAST")
class SpotifySource(val mprisPath: List<String>) : ISource {
    private enum class TrackInfo(val value: String) {
        TRACK_NAME("xesam:title"),
        ARTIST_NAME("xesam:artist"),
        ART_URL("mpris:artUrl"),
        ALBUM_NAME("xesam:album"),
        ALBUM_ARTIST("xesam:albumArtist"),
        TRACK_LENGTH("mpris:length"),
    }

    override fun getPlayer(): String {
        return "${mprisPath.joinToString(".")}.spotify"
    }

    override fun getName(): String {
        return "Spotify"
    }

    override fun extractMetadata(properties: Properties): TrackMetadata {
        // duplicated code idc!!!
        val metadata = properties.Get<Map<String, Any>>(
            "${mprisPath.joinToString(".")}.Player",
            "Metadata"
        )
        val position = properties.Get<Long>(
            "${mprisPath.joinToString(".")}.Player",
            "Position"
        )

        // main metadata
        val trackName : String = metadata[TrackInfo.TRACK_NAME.value] as String
        val trackArtists : List<String> = metadata[TrackInfo.ARTIST_NAME.value] as List<String>
        val albumName : String = metadata[TrackInfo.ALBUM_NAME.value] as String
        val albumArtists : List<String> = metadata[TrackInfo.ALBUM_ARTIST.value] as List<String>
        val trackArt : String = metadata[TrackInfo.ART_URL.value] as String

        // Length
        val lengthSecondProperty: UInt64 = metadata[TrackInfo.TRACK_LENGTH.value] as UInt64
        val lengthSecond = lengthSecondProperty.value().longValueExact()
        val posSeconds = position / 1_000_000.0
        val lenSeconds = lengthSecond / 1_000_000.0

        return TrackMetadata(trackName, trackArtists, albumName,
            albumArtists, trackArt, lenSeconds, posSeconds)
    }
}