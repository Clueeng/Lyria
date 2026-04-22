package cat.psychward.dbus.api.source.impl

import cat.psychward.dbus.api.source.ISource

class SpotifySource : ISource {
    override fun getPlayer(): String {
        return "org.mpris.MediaPlayer2.spotify"
    }

    override fun getName(): String {
        return "Spotify"
    }
}