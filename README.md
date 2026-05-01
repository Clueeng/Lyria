# Lyria
A single-purpose library for all your music related needs

## What does it do
Lyria provides a simple way of fetching metadata from the song you're listening to on Linux systems (running systemd)
See the example [here](https://github.com/Clueeng/Lyria/blob/main/src/test/kotlin/cat/psychward/dbus/DBusInitializerTest.kt)
Mini example:
```kt
dbus = DBusInitializer()
val currentPlayerType = dbus.determinePlayer()
if (currentPlayerType == PlayerType.None) {
    error("No media player detected")
}

val source = dbus.getSource(currentPlayerType)

playerBusName = source.getPlayer()
currentTrack = dbus.getCurrentTrack(source)
```

## Install the library 
To install the library, you need to add the jitpack repository to your build system's config
```kt
repositories {
    maven("https://jitpack.io")
}

const val lyriaVersion: String = "REPLACE-VERSION"
dependencies {
    implementation("com.github.Clueeng:Lyria:$lyriaVersion")
}
```
