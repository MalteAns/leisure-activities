package de.malteans.sosactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class ParticipantDto(
    val userId: String,
    val firstName: String,
    val lastName: String,
)