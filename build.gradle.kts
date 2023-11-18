@file:Suppress("LocalVariableName")

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") apply false
    `java-library`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin")
}

allprojects {
    val release_version: String by project
    version = release_version
    group = "codes.draeger"

    repositories {
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
        withJavadocJar()
        withSourcesJar()
    }

    val includeToPublishing = listOf(
        "core",
        "server-integration-spring-boot",
        "client-integration-okhttp",
    )
    if (this.name in includeToPublishing) {
        apply(plugin = "org.jetbrains.dokka")
        apply(plugin = "maven-publish")
        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    artifactId =
                        if (rootProject.name == project.name) rootProject.name else "${rootProject.name}-${project.name}"
                    from(components["java"])
                    pom {
                        name.set("ReplayGuard")
                        description.set("ReplayGuard is a security-focused library designed to enhance the security of communications between client applications and backend servers. This library addresses the critical need for secure data transmission, specifically targeting the vulnerabilities associated with replay attacks and unauthorized data interception.")
                        url.set("https://github.com/christian-draeger/replay-guard")
                        licenses {
                            license {
                                name.set("MIT License")
                                url.set("https://opensource.org/licenses/MIT")
                            }
                        }
                        developers {
                            developer {
                                id.set("christian-draeger")
                                name.set("Christian Dräger")
                            }
                        }
                        scm {
                            connection.set("scm:git:git://github.com/christian-draeger/replay-guard.git")
                            developerConnection.set("scm:git:ssh://github.com:christian-draeger/replay-guard.git")
                            url.set("https://github.com/christian-draeger/replay-guard")
                        }
                    }
                }
            }
        }

        apply(plugin = "signing")
        signing {
            sign(publishing.publications["mavenJava"])

            val signingKeyId: String? by project
            val signingKey: String? by project
            val signingPassword: String? by project
            useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        }
    }
}

subprojects {
    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
        testImplementation("io.strikt:strikt-core:0.34.0")
        testImplementation("io.mockk:mockk:1.13.8")
    }
    tasks {
        withType<Test> {
            useJUnitPlatform()
        }
    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
}

// do not generate extra load on Nexus with new staging repository if signing fails
val initializeSonatypeStagingRepository by tasks.existing
subprojects {
    initializeSonatypeStagingRepository {
        shouldRunAfter(tasks.withType<Sign>())
    }
}
