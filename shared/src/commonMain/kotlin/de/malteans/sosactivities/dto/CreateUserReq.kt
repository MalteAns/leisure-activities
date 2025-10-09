package de.malteans.sosactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserReq(
    val registrationToken: String,
    val firstName: String,
    val lastName: String,
)