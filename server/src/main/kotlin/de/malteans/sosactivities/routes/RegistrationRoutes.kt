package de.malteans.sosactivities.routes

import de.malteans.sosactivities.RegistrationDto
import de.malteans.sosactivities.RegistrationResp
import de.malteans.sosactivities.RosterDto
import de.malteans.sosactivities.security.Role
import de.malteans.sosactivities.security.UserPrincipal
import de.malteans.sosactivities.security.requirePrincipal
import de.malteans.sosactivities.security.requireRole
import de.malteans.sosactivities.services.RegistrationService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerRegistrationRoutes(
    registrationService: RegistrationService
) {
    authenticate("auth-jwt") {
        route("/activities/{activityId}/registrations") {
            // user self-register
            post {
                val p = call.requirePrincipal<UserPrincipal>()
                val activityId = call.parameters["activityId"]!!
                val status = registrationService.register(activityId, p.userId)
                call.respond(
                    HttpStatusCode.Created,
                    RegistrationResp(status = status)
                )
            }
            // user cancel
            delete {
                val p = call.requirePrincipal<UserPrincipal>()
                val activityId = call.parameters["activityId"]!!
                registrationService.cancel(activityId, p.userId)
                call.respond(HttpStatusCode.NoContent)
            }
            // staff roster
            get {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val activityId = call.parameters["activityId"]!!
                val roster = registrationService.roster(activityId)
                call.respond(
                    RosterDto(
                        confirmed = roster.confirmed.map { RegistrationDto(it.userId, it.name) },
                        waitlist = roster.waitlist.map { RegistrationDto(it.userId, it.name) },
                    )
                )
            }
        }
    }
}
