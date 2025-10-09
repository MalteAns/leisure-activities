package de.malteans.sosactivities.model.ext

import de.malteans.sosactivities.dto.ParticipantDto
import de.malteans.sosactivities.model.Participant

fun Participant.toDto() = ParticipantDto(
    userId = this.userId,
    firstName = this.firstName,
    lastName = this.lastName,
)

fun ParticipantDto.toDomain() = Participant(
    userId = this.userId,
    firstName = this.firstName,
    lastName = this.lastName,
)