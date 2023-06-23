plugins {
    application
}

repositories {
    mavenCentral()
    maven {
        name = "sonatype"
        setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    implementation("com.chrisalbright:ulid:1.0.0-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

application {
    // Define the main class for the application.
    mainClass.set("ulid")
    applicationName = "ulid"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
