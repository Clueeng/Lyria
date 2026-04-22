package cat.psychward.dbus.api.data

class TrackMetadata(
    val name: String,
    val artists: List<String>,
    val album: String,
    val albumArtist: List<String>,
    val artUrl: String,
    val trackLength: Double,
    val trackProgress: Double
    ) {
    companion object {
        fun empty(): TrackMetadata = TrackMetadata(
            name = "",
            artists = emptyList(),
            album = "",
            albumArtist = emptyList(),
            artUrl = "",
            trackLength = 0.0,
            trackProgress = 0.0
        )
    }

    val percentage get() = (trackProgress / trackLength)

    override fun toString(): String {
        return "TrackMeta{trackName=$name, artists=$artists, album=$album, albumArtists=$albumArtist, " +
                "art=$artUrl, length=$trackLength, progress=$trackProgress, percentage=$percentage}"
    }

    override fun equals(other: Any?): Boolean {
        if(other == null || other !is TrackMetadata) return false
        return name.equals(other.name, ignoreCase = true)
                && album.equals(other.album, ignoreCase = true)
                && trackLength == other.trackLength
    }

    override fun hashCode(): Int {
        var result = trackLength.hashCode()
        result = 31 * result + trackProgress.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + artists.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + albumArtist.hashCode()
        result = 31 * result + artUrl.hashCode()
        result = 31 * result + percentage.hashCode()
        return result
    }
}