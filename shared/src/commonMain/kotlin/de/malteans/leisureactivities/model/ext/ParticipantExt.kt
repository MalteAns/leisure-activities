package de.malteans.leisureactivities.model.ext

import de.malteans.leisureactivities.dto.ParticipantDto
import de.malteans.leisureactivities.model.Participant

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