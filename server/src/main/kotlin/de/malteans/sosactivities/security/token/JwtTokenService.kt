package de.malteans.sosactivities.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.malteans.sosactivities.security.Role
import java.util.*

class JwtTokenService: TokenService {

    override fun generate(
        config: TokenConfig,
        subject: String, // Usually UserID
        roles: Set<Role>,
    ): String {
        return JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn)) // TODO: Handle expiration properly

            .withSubject(subject)
            .withClaim("roles", roles.map { it.name })

            .sign(Algorithm.HMAC256(config.secret))
    }
}