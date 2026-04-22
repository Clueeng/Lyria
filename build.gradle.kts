plugins {
    kotlin("jvm") version "2.3.10"
}

group = "cat.psychward.dbus"
version = "1.0-SNAPSHOT"

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
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}