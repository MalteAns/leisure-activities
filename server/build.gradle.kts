plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application

    alias(libs.plugins.jetbrains.kotlin.serialization)
}

group = "de.malteans.sosactivities"
version = "1.0.0"
application {
    mainClass.set("de.malteans.sosactivities.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.auto.head.response)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    // Http Client (for status page images)
    implementation("io.ktor:ktor-client-cio:2.3.7")

    // Database
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.mariadb.jdbc)
    implementation(libs.exposed.java.time) // DateTime support

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    // ###
}