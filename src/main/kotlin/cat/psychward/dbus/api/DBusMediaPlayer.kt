package cat.psychward.dbus.api

import kotlin.system.exitProcess

class DBusMediaPlayer : MediaPlayer {
    override fun raise() {
        println("raising")
        exitProcess(100)
    }

    override fun quit() {
        println("Quitting")
        exitProcess(0)
    }

    override fun getHasTrackList(): Boolean {
        return false
    }

    override fun getObjectPath(): String {
        return "/org/mpris/MediaPlayer2"
    }

    fun getObjectName(): String {
        return "org.mpris.MediaPlayer2"
    }
}