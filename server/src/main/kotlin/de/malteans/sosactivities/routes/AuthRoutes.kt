package de.malteans.sosactivities.routes

import de.malteans.sosactivities.*
import de.malteans.sosactivities.models.ext.roles
import de.malteans.sosactivities.models.ext.toDto
import de.malteans.sosactivities.security.UserPrincipal
import de.malteans.sosactivities.security.requirePrincipal
import de.malteans.sosactivities.security.token.TokenConfig
import de.malteans.sosactivities.security.token.TokenService
import de.malteans.sosactivities.services.UserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerAuthRoutes(
    userService: UserService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    route("/auth") {
        post("/register") {
            val req = call.receive<CreateUserReq>()
            val user = userService.insert(req.id, req.firstName, req.lastName)
                .getOrElse { exception ->
                    if (exception is IllegalArgumentException)
                        return@post call.respond(
                            HttpStatusCode.Conflict,
                            ErrorDto("register_error", exception.message ?: "")
                        )
                    else throw exception // Status Page Plugin will handle this
                }
            call.respond(CreateUserResp(
                user = user.toDto(),
                token = tokenService.generate(tokenConfig, user.id)
            ))
        }

        authenticate("auth-jwt") {
            get("/token/check") { call.respond(mapOf("ok" to true)) }
            get("token/refresh") {
                val principal = call.requirePrincipal<UserPrincipal>()
                val user = userService.get(principal.userId)
                call.respond(UpdateTokenResp(
                    user = user.toDto(),
                    token = tokenService.generate(
                        tokenConfig,
                        subject = user.id,
                        roles = user.roles
                    )
                ))
            }
        }
    }

    authenticate("auth-jwt") {
        get("/me") {
            val principal = call.requirePrincipal<UserPrincipal>()
            val user = userService.get(principal.userId)
            call.respond(user.toDto())
        }
        put("/me") {
            val principal = call.requirePrincipal<UserPrincipal>()
            val req = call.receive<UpdateUserReq>()
            val user = userService.updateName(principal.userId, req.firstName, req.lastName)
            call.respond(user.toDto())
        }
    }
}