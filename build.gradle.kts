plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10"
}

group = "cat.psychward.dbus"
version = "0.1-beta"

val libdbusVersion = "5.2.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // Source: https://mvnrepository.com/artifact/org.freedesktop/libdbus-java
    implementation("com.github.hypfvieh:dbus-java-core:$libdbusVersion")
    // Source: https://mvnrepository.com/artifact/com.github.hypfvieh/dbus-java-transport-native-unixsocket
    implementation("com.github.hypfvieh:dbus-java-transport-native-unixsocket:$libdbusVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}
kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}