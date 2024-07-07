import xyz.wagyourtail.jvmdg.gradle.task.DowngradeJar
import java.io.ByteArrayOutputStream

plugins {
    java
    `maven-publish`
    id("xyz.wagyourtail.jvmdowngrader")
}

allprojects {
    apply(plugin = "java")

    group = "com.leonardobishop"
    version = "3.15"

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
}

subprojects {
    tasks.withType<JavaCompile> {
        options.compilerArgs = listOf("-Xlint:deprecation", "-Xlint:unchecked")
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }

    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }

    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
}

defaultTasks = mutableListOf("clean", "allJar")

tasks.register<Jar>("allJar") {
    subprojects {
        dependsOn.add(tasks.build)
    }

    if (project.findProperty("gitversion") == null || project.findProperty("gitversion") == "true") {
        val gitCommitHash = gitCommitHash()

        allprojects {
            version = "${version}-${gitCommitHash}"
        }
    }

    subprojects {
        configurations.archives {
            allArtifacts.files.forEach {
                from(zipTree(it))
            }
        }
    }

    archiveBaseName = "Quests"
}

fun gitCommitHash(): String {
    val outputStream = ByteArrayOutputStream()

    project.exec {
        commandLine = "git rev-parse --verify --short HEAD".split(" ")
        standardOutput = outputStream
    }

    val gitCommitHashBytes = outputStream.toByteArray()
    return String(gitCommitHashBytes).trim()
}

val javaVersions = listOf(
    // from 1.12 to 1.16.5
    JavaVersion.VERSION_1_8,

    // from 1.17 to 1.17.1
    JavaVersion.VERSION_16,

    // from 1.18 to 1.20.4
    JavaVersion.VERSION_17
)

for (javaVersion in javaVersions) {
    val allJarTask = tasks.getByName<Jar>("allJar")

    // we use this hacky solution to improve display and sort order in IntelliJ Gradle tab
    val majorVersion = javaVersion.ordinal + 1
    val majorVersionFormatted = String.format("%02d", majorVersion)
    val taskName = "downgrade${majorVersionFormatted}AllJar"

    tasks.register<DowngradeJar>(taskName) {
        inputFile = allJarTask.archiveFile
        downgradeTo = javaVersion
        quiet = true

        archiveBaseName = "Quests"
        archiveClassifier = "downgraded-${majorVersion}"
    }

    defaultTasks.add(taskName)
}

artifacts {
    val allJarTask = tasks.named("allJar")
    archives(allJarTask)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.leonardobishop"
            artifactId = "quests"
            version = project.version.toString()

            val allJarTask = tasks.named("allJar")
            artifact(allJarTask)

            pom {
                dependencies {
                    clear()
                }
            }
        }
    }

    repositories {
        maven("https://repo.leonardobishop.com/releases/") {
            credentials {
                username = findPropertyString("mavenUser") ?: System.getenv("MAVEN_USER")
                password = findPropertyString("mavenPassword") ?: System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

fun findPropertyString(propertyName: String): String? {
    val propertyValue = project.findProperty(propertyName)
    return if (propertyValue is String) propertyValue else null
}
