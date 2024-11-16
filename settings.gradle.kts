pluginManagement {
    repositories {
        // io.github.goooler.shadow
        maven("https://plugins.gradle.org/m2/")
        // xyz.wagyourtail.jvmdowngrader
        maven("https://maven.wagyourtail.xyz/releases")
    }

    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
        id("io.github.goooler.shadow") version "8.1.8"
        id("xyz.wagyourtail.jvmdowngrader") version "1.2.1"
    }
}

rootProject.name = "quests"
include("common", "bukkit")
