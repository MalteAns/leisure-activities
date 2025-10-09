package de.malteans.sosactivities.model

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val isStaff: Boolean = false,
    val isAdmin: Boolean = false,
)