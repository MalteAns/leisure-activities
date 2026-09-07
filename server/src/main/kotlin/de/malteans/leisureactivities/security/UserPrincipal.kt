package de.malteans.leisureactivities.security

import de.malteans.leisureactivities.model.Role

data class UserPrincipal(
    val userId: String,
    val roles: Set<Role>
)