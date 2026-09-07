package de.malteans.leisureactivities.routes

import de.malteans.leisureactivities.dto.SetStaffReq
import de.malteans.leisureactivities.dto.SetStaffResp
import de.malteans.leisureactivities.model.Role
import de.malteans.leisureactivities.model.ext.toDto
import de.malteans.leisureactivities.security.requireRole
import de.malteans.leisureactivities.services.UserService
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