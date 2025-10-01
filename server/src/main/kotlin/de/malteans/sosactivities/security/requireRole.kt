package de.malteans.sosactivities.security

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.requireRole(vararg allowed: Role): UserPrincipal {
    val p = requirePrincipal<UserPrincipal>()
    if (p.roles.none { it in allowed.toSet() }) {
        respond(HttpStatusCode.Forbidden, mapOf("error" to "forbidden"))
        throw AbortRoute // short-circuit
    }
    return p
}