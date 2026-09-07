package de.malteans.leisureactivities.security.token

import de.malteans.leisureactivities.model.Role

interface TokenService {
    fun generate(
        config: TokenConfig,
        subject: String,
        roles: Set<Role> = emptySet(),
    ): String
}