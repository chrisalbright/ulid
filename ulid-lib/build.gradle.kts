group = "io.github.chrisalbright"
base {
    archivesName.set("ulid")
}
version = "1.0.0-SNAPSHOT"

plugins {
    `java-library`
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
    maven {
        name = "OSSRH"
        setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
        credentials {
            username = System.getenv("OSSRH_USER") ?: return@credentials
            password = System.getenv("OSSRH_PASSWORD") ?: return@credentials
        }
    }
    maven {
        name = "OSSRH-Snapshots"
        setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        credentials {
            username = System.getenv("OSSRH_USER") ?: return@credentials
            password = System.getenv("OSSRH_PASSWORD") ?: return@credentials
        }
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        val main by creating(MavenPublication::class) {
            from(components["java"])
            pom {
                name.set("ulid")
                description.set("A ulid library for the JVM")
                url.set("https://github.com/chrisalbright/ulid")
                developers {
                    developer {
                        id.set("chrisalbright")
                        name.set("Chris Albright")
                        email.set("calbright@gmail.com")
                    }
                }
                scm {
                    connection.set("https://github.com/chrisalbright/ulid.git")
                    developerConnection.set("git@github.com:chrisalbright/ulid.git")
                    url.set("https://github.com/chrisalbright/ulid/tree/main")
                }
            }
        }
    }
}

signing {
    val key = System.getenv("SIGNING_KEY")
    val password = System.getenv("SIGNING_PASSWORD")
    val publishing: PublishingExtension by project

    useInMemoryPgpKeys(key, password)
    sign(publishing.publications)
}

tasks {
    test {
        useJUnitPlatform()
        testLogging.showExceptions = true
    }
}
