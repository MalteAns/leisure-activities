package de.malteans.leisureactivities.routes

import de.malteans.leisureactivities.dto.SignUpResp
import de.malteans.leisureactivities.model.Role
import de.malteans.leisureactivities.model.ext.toDto
import de.malteans.leisureactivities.security.UserPrincipal
import de.malteans.leisureactivities.security.requirePrincipal
import de.malteans.leisureactivities.security.requireRole
import de.malteans.leisureactivities.services.SignUpService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerSignUpRoutes(
    signUpService: SignUpService
) {
    authenticate("auth-jwt") {
        route("/activities/{activityId}/signup") {
            // user self-signup
            post {
                val p = call.requirePrincipal<UserPrincipal>()
                val activityId = call.parameters["activityId"]!!
                val status = signUpService.register(activityId, p.userId)
                call.respond(
                    HttpStatusCode.Created,
                    SignUpResp(status = status)
                )
            }
            // user cancel
            delete {
                val p = call.requirePrincipal<UserPrincipal>()
                val activityId = call.parameters["activityId"]!!
                signUpService.cancel(activityId, p.userId)
                call.respond(HttpStatusCode.NoContent)
            }
            // staff roster
            get {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val activityId = call.parameters["activityId"]!!
                val roster = signUpService.roster(activityId)
                call.respond(roster.toDto())
            }
        }
    }
}
