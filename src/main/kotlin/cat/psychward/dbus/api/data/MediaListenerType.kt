package cat.psychward.dbus.api.data

enum class MediaListenerType(val code: String) {
    PAUSE("Paused"),
    RESUME("Playing"),
    STOP("Stopped"),
    CHANGED("Changed"),
    NEXT_LYRIC("NextLyric"),;
}