package de.malteans.sosactivities.routes

import de.malteans.sosactivities.dto.*
import de.malteans.sosactivities.model.ext.roles
import de.malteans.sosactivities.model.ext.toDto
import de.malteans.sosactivities.security.UserPrincipal
import de.malteans.sosactivities.security.requirePrincipal
import de.malteans.sosactivities.security.token.TokenConfig
import de.malteans.sosactivities.security.token.TokenService
import de.malteans.sosactivities.services.RegistrationTokensService
import de.malteans.sosactivities.services.UserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.registerUserRoutes(
    registrationTokensService: RegistrationTokensService,
    userService: UserService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
) {
    route("/auth") {
        post("/register") {
            val req = call.receive<CreateUserReq>()
            val tokenId = registrationTokensService.getActiveTokenId(req.registrationToken)
                ?: return@post call.respond(HttpStatusCode.Unauthorized)
            var randomId = UUID.randomUUID().toString()
            var response = userService.insert(
                id = randomId,
                firstName = req.firstName,
                lastName = req.lastName,
                tokenId = tokenId,
            )
            while (response.isFailure) {
                response.exceptionOrNull()?.printStackTrace()
                randomId = UUID.randomUUID().toString()
                response = userService.insert(
                    id = randomId,
                    firstName = req.firstName,
                    lastName = req.lastName,
                    tokenId = tokenId,
                )
            }
            val user = response.getOrThrow()
            call.respond(
                CreateUserResp(
                    user = user.toDto(),
                    token = tokenService.generate(tokenConfig, user.id)
                )
            )
        }

        authenticate("auth-jwt") {
            get("/token/check") { call.respond(mapOf("ok" to true)) }
            get("/token/refresh") {
                val principal = call.requirePrincipal<UserPrincipal>()
                val user = userService.get(principal.userId)
                call.respond(
                    RefreshTokenResp(
                        user = user.toDto(),
                        token = tokenService.generate(
                            config = tokenConfig,
                            subject = user.id,
                            roles = user.roles
                        )
                    )
                )
            }
        }
    }

    authenticate("auth-jwt") {
        route("/me") {
            get {
                val principal = call.requirePrincipal<UserPrincipal>()
                val user = userService.get(principal.userId)
                call.respond(user.toDto())
            }
            put {
                val principal = call.requirePrincipal<UserPrincipal>()
                val req = call.receive<UpdateUserReq>()
                val user = userService.updateName(principal.userId, req.firstName, req.lastName)
                call.respond(user.toDto())
            }
            get("/signups") {
                val principal = call.requirePrincipal<UserPrincipal>()
                val signUps = userService.getSignUps(principal.userId)
                call.respond(MySignUpsResp(activityIds = signUps))
            }
        }
    }
}