package de.malteans.sosactivities.security.token

import de.malteans.sosactivities.security.Role

interface TokenService {
    fun generate(
        config: TokenConfig,
        subject: String,
        roles: Set<Role> = emptySet(),
    ): String
}