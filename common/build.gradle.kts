plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    // Use it for nullability annotations
    api("org.jspecify:jspecify:1.0.0")

    // Use it for contracts and unmodifiability annotations
    api("org.jetbrains:annotations:26.0.2")

    // Testing dependencies
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    maxHeapSize = "1G"

    testLogging {
        events("passed")
    }
}
