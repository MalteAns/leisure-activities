package de.malteans.sosactivities.security.token

import de.malteans.sosactivities.model.Role

interface TokenService {
    fun generate(
        config: TokenConfig,
        subject: String,
        roles: Set<Role> = emptySet(),
    ): String
}