package de.malteans.sosactivities.routes

import de.malteans.sosactivities.dto.CreateActivityReq
import de.malteans.sosactivities.dto.DeleteActivitiesReq
import de.malteans.sosactivities.dto.UpdateActivityReq
import de.malteans.sosactivities.model.Role
import de.malteans.sosactivities.model.ext.toDto
import de.malteans.sosactivities.security.requireRole
import de.malteans.sosactivities.services.ActivityService
import de.malteans.sosactivities.services.ImageService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerActivityRoutes(
    activityService: ActivityService,
    imageService: ImageService,
) {
    authenticate("auth-jwt") {
        route("/activities") {
            get {
                val from = call.request.queryParameters["from"]
                val to = call.request.queryParameters["to"]
                val list = activityService.list(from, to).map { activity -> activity.toDto(
                    activity.imageId?.let { imageService.getUrl(it) }
                ) }
                call.respond(list)
            }
            get("{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val activity = activityService.get(id) ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(activity.toDto(
                    activity.imageId?.let { imageService.getUrl(it) }
                ))
            }
            post {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val req = call.receive<CreateActivityReq>()
                val createdActivity = activityService.create(req)
                call.respond(HttpStatusCode.Created, createdActivity.toDto(
                    createdActivity.imageId?.let { imageService.getUrl(it) }
                ))
            }
            delete {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val req = call.receive<DeleteActivitiesReq>()
                req.activityIds.forEach { id ->
                    activityService.delete(id)
                }
                call.respond(HttpStatusCode.NoContent)
            }
            patch("{id}") {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val id = call.parameters["id"]!!
                val updateActivityReq = call.receive<UpdateActivityReq>()
                val updatedActivity = activityService.update(
                    id, updateActivityReq
                ) ?: return@patch call.respond(HttpStatusCode.NotFound)
                call.respond(updatedActivity.toDto(
                    updatedActivity.imageId?.let { imageService.getUrl(it) }
                ))
            }
            delete("{id}") {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val id = call.parameters["id"]!!
                activityService.delete(id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
