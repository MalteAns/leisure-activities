package de.malteans.leisureactivities.security

import de.malteans.leisureactivities.model.Role
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.requireRole(vararg allowed: Role): UserPrincipal {
    val principal = requirePrincipal<UserPrincipal>()
    if (principal.roles.none { it in allowed.toSet() }) {
        respond(HttpStatusCode.Forbidden, mapOf("error" to "forbidden"))
        throw AbortRoute // short-circuit
    }
    return principal
}