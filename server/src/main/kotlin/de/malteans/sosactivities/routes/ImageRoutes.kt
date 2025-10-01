package de.malteans.sosactivities.routes

import de.malteans.sosactivities.FinalizeResp
import de.malteans.sosactivities.PresignReq
import de.malteans.sosactivities.PresignResp
import de.malteans.sosactivities.security.Role
import de.malteans.sosactivities.security.requireRole
import de.malteans.sosactivities.services.ImageService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerImageRoutes(
    imageService: ImageService
) {
    authenticate("auth-jwt") {
        route("/images") {
            post("presign") {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val req = call.receive<PresignReq>()
                val ps = imageService.presignUpload(req)
                call.respond(PresignResp(ps.id, ps.putUrl, ps.key))
            }
            post("{id}/finalize") {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val id = call.parameters["id"]!!
                val meta = imageService.finalize(id)
                call.respond(FinalizeResp(meta.id, meta.publicUrl, meta.width, meta.height))
            }
            get {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val mine = call.request.queryParameters["mine"]?.toBoolean() ?: false
                val list = imageService.list(mine)
                call.respond(list) // return your ImageDto list
            }
            delete("{id}") {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val id = call.parameters["id"]!!
                imageService.delete(id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
