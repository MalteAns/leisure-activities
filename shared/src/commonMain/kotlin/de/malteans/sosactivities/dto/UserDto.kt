package de.malteans.sosactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val isStaff: Boolean,
    val isAdmin: Boolean
)

