package de.malteans.leisureactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserReq(
    val registrationToken: String,
    val firstName: String,
    val lastName: String,
)