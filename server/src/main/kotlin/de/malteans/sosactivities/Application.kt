package de.malteans.sosactivities

import de.malteans.sosactivities.di.module
import de.malteans.sosactivities.plugins.configureRequestValidation
import de.malteans.sosactivities.plugins.configureRouting
import de.malteans.sosactivities.security.AbortRoute
import de.malteans.sosactivities.security.installJwtAuth
import de.malteans.sosactivities.security.token.TokenConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(Netty, port = Constants.SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

@Suppress("unused")
fun Application.module() {
    val httpClient = HttpClient(CIO)

    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(CallLogging)
    install(ContentNegotiation) { json() }
    configureRequestValidation()
    install(StatusPages) {
        suspend fun respondCat(call: ApplicationCall, status: HttpStatusCode) {
            val bytes: ByteArray = httpClient.get("https://http.cat/${status.value}.jpg").body()
            call.respondBytes(bytes, ContentType.Image.JPEG, status)
        }

        exception<AbortRoute> { call, _ -> /* already responded */ }
        exception<Throwable> { call, cause ->
            // TODO: log cause
            cause.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, ErrorDto("server_error", cause.message ?: ""))
        }

        status(
            HttpStatusCode.BadRequest, HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden,
            HttpStatusCode.NotFound, HttpStatusCode.InternalServerError
        ) { call, status ->
            if (status.value in 400..599) {
                respondCat(call, status)
            }
        }
    }

    val applicationConfig: ApplicationConfig = environment.config
    // DI
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single { applicationConfig } // Application
            },
            module
        )
    }

    val tokenConfig = TokenConfig(
        secret = System.getenv("JWT_SECRET"),
    )

    installJwtAuth(tokenConfig)

    configureRouting(tokenConfig)
}

// Shared error envelope
@Serializable data class ErrorDto(val code: String, val message: String)
