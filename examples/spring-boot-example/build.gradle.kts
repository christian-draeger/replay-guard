plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(project(":server-integration-spring-boot"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(project(":core")) // TODO: find out why this is necessary, usually it should be transitive
}

tasks.withType<Test> {
    useJUnitPlatform()
}
