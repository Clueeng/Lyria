package cat.psychward.dbus.api.data

enum class TrackInfo(val value: String) {
    TRACK_NAME("xesam:title"),
    ARTIST_NAME("xesam:artist"),
    ART_URL("mpris:artUrl"),
    ALBUM_NAME("xesam:album"),
    ALBUM_ARTIST("xesam:albumArtist"),
}