plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10"
//    id("com.gradleup.shadow") version "8.3.6"
//    id("com.gradleup.shadow") version "8.1.1"
//    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.gradleup.shadow") version "8.3.5"
    `maven-publish`
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


tasks.shadowJar {
    // Relocate each dependency to avoid conflicts
//    relocate("org.freedesktop.dbus", "cat.psychward.dbus.shaded.dbus")
//    relocate("com.github.hypfvieh", "cat.psychward.dbus.shaded.hypfvieh")
//    relocate("kotlinx.serialization", "cat.psychward.dbus.shaded.kotlinx.serialization")

    mergeServiceFiles()
    archiveClassifier.set("")

}

tasks.build {
    dependsOn(tasks.shadowJar)
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            project.shadow.component(this)
        }
    }
}

tasks.test {
    useJUnitPlatform()
}