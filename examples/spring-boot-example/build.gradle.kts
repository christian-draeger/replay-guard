plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(project(":server-integration-spring-boot"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(project(":core")) // TODO: find out why this is necessary, usually it should be transitive
}
