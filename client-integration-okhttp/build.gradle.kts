plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation(project(":core"))
}
