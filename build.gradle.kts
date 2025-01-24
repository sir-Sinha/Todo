
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")  // Content negotiation
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")


    implementation("io.ktor:ktor-server-auth:2.3.7")          // Authentication support
    implementation("io.ktor:ktor-server-auth-jwt:2.3.7")      // JWT authentication
    implementation("org.mindrot:jbcrypt:0.4")                 // Password hashing

    // Database dependencies
    implementation("org.postgresql:postgresql:42.7.1")        // PostgreSQL driver
    implementation("org.jetbrains.exposed:exposed-core:0.45.0")    // SQL framework
    implementation("org.jetbrains.exposed:exposed-dao:0.45.0")     // DAO support
    implementation("org.jetbrains.exposed:exposed-jdbc:0.45.0")    // JDBC support
    implementation("com.zaxxer:HikariCP:5.1.0")              // Connection pooling

    implementation("io.ktor:ktor-server-content-negotiation:2.2.3")

    // Kotlinx Serialization dependencies
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    // For handling serialization with JSON (Optional, for testing purposes)
    testImplementation("io.ktor:ktor-server-tests:2.2.3")

}
