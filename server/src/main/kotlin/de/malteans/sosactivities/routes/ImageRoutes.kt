package de.malteans.sosactivities.routes

import de.malteans.sosactivities.dto.FinalizeResp
import de.malteans.sosactivities.dto.ImagePresignReq
import de.malteans.sosactivities.dto.PresignResp
import de.malteans.sosactivities.model.Role
import de.malteans.sosactivities.security.requireRole
import de.malteans.sosactivities.services.ImageService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*

fun Route.registerImageRoutes(
    imageService: ImageService
) {
    authenticate("auth-jwt") {
        route("/images") {
            post("presign") {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val req = call.receive<ImagePresignReq>()
                val ps = imageService.presignUpload(req)
                call.respond(PresignResp(ps.id, ps.putUrl, ps.key))
            }
            put("{id}/upload") {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val id = call.parameters["id"]!!
                val bytes = call.receiveChannel().toByteArray() // requires kotlinx-io ext
                imageService.saveFile(id, bytes)
                call.respond(HttpStatusCode.Created)
            }
            post("{id}/finalize") {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val id = call.parameters["id"]!!
                val meta = imageService.finalize(id)
                call.respond(FinalizeResp(meta.id, meta.publicUrl, meta.width, meta.height))
            }
            get {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val list = imageService.list()
                call.respond(list)
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
