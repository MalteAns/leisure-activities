package de.malteans.sosactivities.plugins

import de.malteans.sosactivities.routes.*
import de.malteans.sosactivities.security.token.TokenConfig
import de.malteans.sosactivities.security.token.TokenService
import de.malteans.sosactivities.services.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File


fun Application.configureRouting(
    tokenConfig: TokenConfig,
) {
    val registrationTokensService: RegistrationTokensService by inject()
    val userService: UserService by inject()
    val tokenService: TokenService by inject()
    val activityService: ActivityService by inject()
    val signUpService: SignUpService by inject()
    val imageService: ImageService by inject()
    routing {
        staticFiles("/uploads", File("serverUploads"))
        route("/v1") {
            registrationTokensRoutes(
                registrationTokensService = registrationTokensService,
            )
            registerUserRoutes(
                registrationTokensService = registrationTokensService,
                userService = userService,
                tokenService = tokenService,
                tokenConfig = tokenConfig,
            )
            registerAdministrationRoutes(
                userService = userService
            )
            registerActivityRoutes(
                activityService = activityService,
                imageService = imageService,
            )
            registerSignUpRoutes(
                signUpService = signUpService
            )
            registerImageRoutes(
                imageService = imageService
            )
            get("/health") { call.respond(mapOf("status" to "ok")) }
        }
    }
}