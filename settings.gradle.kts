pluginManagement {
    repositories {
        // io.github.goooler.shadow
        maven("https://plugins.gradle.org/m2/")
        // xyz.wagyourtail.jvmdowngrader
        maven("https://maven.wagyourtail.xyz/releases")
    }

    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
        id("com.gradleup.shadow") version "9.2.2"
        id("xyz.wagyourtail.jvmdowngrader") version "1.3.4"
    }
}

rootProject.name = "quests"
include("common", "bukkit")
