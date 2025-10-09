package de.malteans.sosactivities.model.ext

import de.malteans.sosactivities.dto.RosterDto
import de.malteans.sosactivities.model.Roster

fun Roster.toDto() = RosterDto(
    confirmed = this.confirmed.map { it.toDto() },
    waitlist = this.waitlist.map { it.toDto() },
)

fun RosterDto.toDomain() = Roster(
    confirmed = this.confirmed.map { it.toDomain() },
    waitlist = this.waitlist.map { it.toDomain() },
)