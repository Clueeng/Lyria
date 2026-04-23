package cat.psychward.dbus

import cat.psychward.dbus.api.PlayerType
import cat.psychward.dbus.api.data.MediaListenerType
import cat.psychward.dbus.api.data.TrackMetadata
import cat.psychward.dbus.api.player.source.ISource
import cat.psychward.dbus.api.player.source.impl.BrowserSource
import cat.psychward.dbus.api.player.source.impl.SpotifySource
import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder
import org.freedesktop.dbus.interfaces.DBus
import org.freedesktop.dbus.interfaces.Properties
import javax.xml.transform.Source
import kotlin.jvm.java

@Suppress("UNCHECKED_CAST")
class DBusInitializer(val mprisPath: List<String> = listOf("org", "mpris", "MediaPlayer2")) {
    val bus : DBusConnection = DBusConnectionBuilder
        .forSessionBus().build()

    fun isRunning(app: String) : Boolean {
        val bus = DBusConnectionBuilder.forSessionBus().build()
        val dbus = bus.getRemoteObject(
            "org.freedesktop.DBus",
            "/org/freedesktop/DBus",
            DBus::class.java
        )

        val names = dbus.ListNames().filter { it.startsWith(mprisPath.joinToString(".")) }
        names.forEach {
            if(it.contains(app)) return true
        }
        return false
    }

    fun determinePlayer() : PlayerType {
        if(isRunning("spotify")) return PlayerType.Spotify
        if(isRunning("firefox")
            || isRunning("chrome")) return PlayerType.Browser
        println("Could not find any player")
        return PlayerType.None
    }

    fun listenToPlayerChanges(playerBusName: String, listenerType: MediaListenerType, run: () -> Unit) {
        val player = bus.getRemoteObject(
            playerBusName,
            "/org/mpris/MediaPlayer2",
            Properties::class.java
        )

        bus.addSigHandler(
            Properties.PropertiesChanged::class.java,
            player
        ) { signal ->
            listen(signal, listenerType, run)
        }
    }

    fun listen(signal: Properties.PropertiesChanged, listenerType: MediaListenerType, run: () -> Unit) {
        val changed = signal.propertiesChanged

        for ((key, value) in changed) {
            when (key) {
                "PlaybackStatus" -> {
                    val status = value.value as String
                    if(status == listenerType.code) {
                        run.invoke()
                    }
                }
                "Metadata" -> {
                    if(listenerType == MediaListenerType.CHANGED) {
                        run.invoke()
                    }
                }
            }
        }
    }

    fun position(playerName: String): Double {
        val properties = bus.getRemoteObject(
            playerName,
            "/${mprisPath.joinToString("/")}",
            Properties::class.java
        )
        val position = properties.Get(
            "org.mpris.MediaPlayer2.Player",
            "Position"
        ) as Long

        return position / 1_000_000.0
    }

    fun getSource(type: PlayerType): ISource {
        return when(type) {
            PlayerType.Spotify -> SpotifySource(mprisPath)
            PlayerType.Browser -> {
                val dbus = bus.getRemoteObject(
                    "org.freedesktop.DBus",
                    "/org/freedesktop/DBus",
                    DBus::class.java
                )
                val names = dbus.ListNames().filter {
                    it.startsWith("org.mpris.MediaPlayer2.") && !it.contains("spotify")
                }
                val name = names[0].replace("org.mpris.MediaPlayer2.", "")
                BrowserSource(mprisPath, name)
            }
            else -> {
                error("Could not find any source")
            }
        }

    }

    fun getCurrentTrack(source: ISource) : TrackMetadata {
        val playerName = source.getPlayer()
        val properties = bus.getRemoteObject(
            playerName,
            "/${mprisPath.joinToString("/")}",
            Properties::class.java
        )
        return source.extractMetadata(properties)
    }
}