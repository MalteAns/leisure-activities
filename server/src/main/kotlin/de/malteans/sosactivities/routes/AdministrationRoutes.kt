package de.malteans.sosactivities.routes

import de.malteans.sosactivities.SetStaffReq
import de.malteans.sosactivities.SetStaffResp
import de.malteans.sosactivities.models.ext.toDto
import de.malteans.sosactivities.security.Role
import de.malteans.sosactivities.security.requireRole
import de.malteans.sosactivities.services.UserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerAdministrationRoutes(
    userService: UserService
) {
    authenticate("auth-jwt") {
        route("/admin") {
            post("setStuff") {
                call.requireRole(Role.ADMIN)
                val req = call.receive<SetStaffReq>()
                val user = userService.updateIsStaff(req.userId, req.setStuff)
                call.respond(
                    HttpStatusCode.OK,
                    SetStaffResp(user.toDto())
                )
            }
        }
    }
}