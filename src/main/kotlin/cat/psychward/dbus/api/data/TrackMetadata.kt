package cat.psychward.dbus.api.data

import cat.psychward.dbus.api.player.lyrics.TrackLyrics

class TrackMetadata(
    val name: String,
    val artists: List<String>,
    val album: String,
    val albumArtist: List<String>,
    val artUrl: String,
    val trackLength: Double,
    var trackProgress: Double
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
    val lyrics = TrackLyrics(this)

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

    fun formatProgressMs(): String {
        val totalMillis = (trackProgress * 1000).toLong()
        val minutes = totalMillis / 60_000
        val seconds = (totalMillis % 60_000) / 1000
        val millis = totalMillis % 1000
        return "%02d:%02d.%03d".format(minutes, seconds, millis)
    }
    fun formatProgress(): String {
        val totalSeconds = trackProgress.toLong()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    fun setProgressFromPlayer(positionSeconds: Double) {
        trackProgress = positionSeconds
    }



    @Volatile
    private var lastLyric: String? = null

    fun listenLyricsChanges(onChange: (String?) -> Unit) {
        Thread {
            while (true) {
                val current = lyrics.getCurrentLyric(fromSeconds(trackProgress))
                if (current != lastLyric) {
                    lastLyric = current
                    onChange(current)
                }
                Thread.sleep(100)
            }
        }.start()
    }
}