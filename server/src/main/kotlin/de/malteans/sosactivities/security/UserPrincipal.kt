package de.malteans.sosactivities.security

data class UserPrincipal(
    val userId: String,
    val roles: Set<Role>
)