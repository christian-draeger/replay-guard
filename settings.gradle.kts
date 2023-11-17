pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("org.springframework.boot") version "3.1.5"
        id("io.spring.dependency-management") version "1.1.4"
        kotlin("plugin.spring") version "1.9.20"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "replay-guard"
include("core")
include("server-integration-spring-boot")
include("examples:spring-boot-example")
findProject(":examples:spring-boot-example")?.name = "spring-boot-example"
