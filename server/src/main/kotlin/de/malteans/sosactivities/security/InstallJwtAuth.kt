package de.malteans.sosactivities.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.malteans.sosactivities.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.installJwtAuth(config: TokenConfig) {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .build()
            )
            validate { cred ->
                val sub = cred.subject ?: return@validate null // usually the UserID
                val roles = cred.payload.getClaim("roles")?.asList(String::class.java)
                    ?.mapNotNull { runCatching { Role.valueOf(it) }.getOrNull() }
                    ?.toSet() ?: emptySet()
                UserPrincipal(sub, roles)
            }
        }
    }
}
