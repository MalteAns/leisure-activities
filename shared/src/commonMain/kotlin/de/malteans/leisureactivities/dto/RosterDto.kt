package de.malteans.leisureactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class RosterDto(
    val confirmed: List<ParticipantDto>,
    val waitlist: List<ParticipantDto> = emptyList()
)