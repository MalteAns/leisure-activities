package de.malteans.sosactivities.plugins

import de.malteans.sosactivities.routes.*
import de.malteans.sosactivities.security.token.TokenConfig
import de.malteans.sosactivities.security.token.TokenService
import de.malteans.sosactivities.services.ActivityService
import de.malteans.sosactivities.services.ImageService
import de.malteans.sosactivities.services.RegistrationService
import de.malteans.sosactivities.services.UserService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.configureRouting(
    tokenConfig: TokenConfig,
) {
    val userService: UserService by inject()
    val tokenService: TokenService by inject()
    val activityService: ActivityService by inject()
    val registrationService: RegistrationService by inject()
    val imageService: ImageService by inject()
    routing {
        route("/v1") {
            registerAuthRoutes(
                userService = userService,
                tokenService = tokenService,
                tokenConfig = tokenConfig,
            )
            registerAdministrationRoutes(
                userService = userService
            )
            registerActivityRoutes(
                activityService = activityService
            )
            registerRegistrationRoutes(
                registrationService = registrationService
            )
            registerImageRoutes(
                imageService = imageService
            )
            get("/health") { call.respond(mapOf("status" to "ok")) }
        }
    }
}