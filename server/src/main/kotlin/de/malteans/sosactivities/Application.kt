package de.malteans.sosactivities

import de.malteans.sosactivities.di.module
import de.malteans.sosactivities.plugins.configureRequestValidation
import de.malteans.sosactivities.plugins.configureRouting
import de.malteans.sosactivities.plugins.configureStatusPages
import de.malteans.sosactivities.security.installJwtAuth
import de.malteans.sosactivities.security.token.TokenConfig
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(Netty, port = Constants.SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

@Suppress("unused")
fun Application.module() {
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(CallLogging)
    install(ContentNegotiation) { json() }
    configureRequestValidation()
    configureStatusPages()

//    val applicationConfig: ApplicationConfig = environment.config
    // DI
    install(Koin) {
        slf4jLogger()
        modules(
//            module {
//                single { applicationConfig } // Application
//            },
            module
        )
    }

    val tokenConfig = TokenConfig(
        secret = System.getenv("JWT_SECRET"),
    )

    installJwtAuth(tokenConfig)

    configureRouting(tokenConfig)
}
