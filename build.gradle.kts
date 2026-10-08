
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
}

group = "com.sdevprem"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}
dependencies {
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio)
    implementation(ktorLibs.server.netty)
    implementation(libs.logback.classic)

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)

    implementation("androidx.compose.remote:remote-core:1.0.0-alpha20")
    implementation("androidx.compose.remote:remote-creation:1.0.0-alpha20")
    implementation("androidx.compose.remote:remote-creation-core:1.0.0-alpha20")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("androidx.compose.remote:remote-creation-jvm:1.0.0-alpha20")
//    implementation("androidx.compose.remote:remote-creation-compose:1.0.0-alpha20")
}
