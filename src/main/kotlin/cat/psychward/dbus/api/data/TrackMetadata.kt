package cat.psychward.dbus.api.data

class TrackMetadata(
    val name: String,
    val artists: List<String>,
    val album: String,
    val albumArtist: List<String>,
    val artUrl: String,
    ) {

    override fun toString(): String {
        return "TrackMeta{trackName=$name, artists=$artists, album=$album, albumArtists=$albumArtist, art=$artUrl}"
    }
}