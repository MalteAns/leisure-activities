package de.malteans.sosactivities.routes

import de.malteans.sosactivities.ActivityDto
import de.malteans.sosactivities.CreateActivityReq
import de.malteans.sosactivities.models.Activity
import de.malteans.sosactivities.security.Role
import de.malteans.sosactivities.security.requireRole
import de.malteans.sosactivities.services.ActivityService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerActivityRoutes(
    activityService: ActivityService
) {
    route("/activities") {
        get {
            val from = call.request.queryParameters["from"]
            val to = call.request.queryParameters["to"]
            val list = activityService.list(from, to).map { it.toDto() }
            call.respond(list)
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val a = activityService.get(id) ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(a.toDto())
        }

        authenticate("auth-jwt") {
            post {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val req = call.receive<CreateActivityReq>()
                val created = activityService.create(req)
                call.respond(HttpStatusCode.Created, created.toDto())
            }
            patch("{id}") {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val id = call.parameters["id"]!!
                val body = call.receive<Map<String, @JvmSuppressWildcards Any?>>()
                val upd = activityService.patch(id, body) ?: return@patch call.respond(HttpStatusCode.NotFound)
                call.respond(upd.toDto())
            }
            delete("{id}") {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val id = call.parameters["id"]!!
                activityService.delete(id)
                call.respond(HttpStatusCode.NoContent)
            }
            patch("{id}/image") {
                call.requireRole(Role.STAFF, Role.ADMIN)
                val id = call.parameters["id"]!!
                val imageId = call.receive<Map<String, String>>()["imageId"]
                    ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val upd = activityService.setImage(id, imageId) ?: return@patch call.respond(HttpStatusCode.NotFound)
                call.respond(upd.toDto())
            }
        }
    }
}

// mappers (could live beside your models)
private fun Activity.toDto() = ActivityDto(
    id = id, title = title, description = description,
    startsAt = startsAt.toString(), durationMin = durationMin, imageId = imageId
)
