package cat.psychward.dbus.api.player.source.impl

import cat.psychward.dbus.api.data.TrackMetadata
import cat.psychward.dbus.api.player.source.ISource
import org.freedesktop.dbus.interfaces.Properties

@Suppress("UNCHECKED_CAST")
class BrowserSource(val mprisPath: List<String>, val browser: String) : ISource {
    private enum class TrackInfo(val value: String) {
        TRACK_NAME("xesam:title"),
        CACHED_ART_PATH("mpris:artUrl"),
        ARTIST_NAME("xesam:artist"),
        ALBUM_NAME("xesam:album"),
        TRACK_LENGTH("mpris:length"),
    }

    override fun getPlayer(): String {
        return "${mprisPath.joinToString(".")}.${browser}"
    }

    override fun getName(): String {
        return browser[0].uppercase() + browser.substring(1)
    }

    override fun extractMetadata(properties: Properties): TrackMetadata {
        val metadata = properties.Get<Map<String, Any>>(
            "${mprisPath.joinToString(".")}.Player",
            "Metadata"
        )
        val position = properties.Get<Long>(
            "${mprisPath.joinToString(".")}.Player",
            "Position"
        )
        // meta
        val trackName : String = metadata[TrackInfo.TRACK_NAME.value] as String
        val trackArtists : List<String> = metadata[TrackInfo.ARTIST_NAME.value] as List<String>
        val albumName : String = metadata[TrackInfo.ALBUM_NAME.value] as String
        val albumArt : String = metadata[TrackInfo.CACHED_ART_PATH.value] as String
        // length
        val length: Long = metadata[TrackInfo.TRACK_LENGTH.value] as Long
        val lengthSeconds = length / 1_000_000.0
        val positionSeconds = position / 1_000_000.0
        return TrackMetadata(trackName, trackArtists, albumName, trackArtists, albumArt,
            lengthSeconds, positionSeconds)
    }
}