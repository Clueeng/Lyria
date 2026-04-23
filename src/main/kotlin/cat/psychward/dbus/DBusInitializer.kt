package cat.psychward.dbus

import cat.psychward.dbus.api.PlayerType
import cat.psychward.dbus.api.data.TrackMetadata
import cat.psychward.dbus.api.player.source.ISource
import cat.psychward.dbus.api.player.source.impl.BrowserSource
import cat.psychward.dbus.api.player.source.impl.SpotifySource
import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder
import org.freedesktop.dbus.interfaces.DBus
import org.freedesktop.dbus.interfaces.Properties
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


    fun getCurrentTrack(type: PlayerType) : TrackMetadata {
        val source: ISource = when(type) {
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
                error("Could not find any player")
            }
        }
        val playerName = source.getPlayer()
        val properties = bus.getRemoteObject(
            playerName,
            "/${mprisPath.joinToString("/")}",
            Properties::class.java
        )
        println("properties=$properties")
        return source.extractMetadata(properties)
    }
}