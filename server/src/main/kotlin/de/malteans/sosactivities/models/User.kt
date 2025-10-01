package de.malteans.sosactivities.models

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val isStaff: Boolean = false,
    val isAdmin: Boolean = false,
)
