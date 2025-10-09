package de.malteans.sosactivities.security

import de.malteans.sosactivities.model.Role

data class UserPrincipal(
    val userId: String,
    val roles: Set<Role>
)