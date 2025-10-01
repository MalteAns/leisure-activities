package de.malteans.sosactivities.security

import io.ktor.server.application.*
import io.ktor.server.auth.*

// Helpers
suspend inline fun <reified P : UserPrincipal> ApplicationCall.requirePrincipal(): P {
    return principal<P>() ?: error("Missing principal")
}