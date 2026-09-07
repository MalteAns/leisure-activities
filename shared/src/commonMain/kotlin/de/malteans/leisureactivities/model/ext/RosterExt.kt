package de.malteans.leisureactivities.model.ext

import de.malteans.leisureactivities.dto.RosterDto
import de.malteans.leisureactivities.model.Roster

fun Roster.toDto() = RosterDto(
    confirmed = this.confirmed.map { it.toDto() },
    waitlist = this.waitlist.map { it.toDto() },
)

fun RosterDto.toDomain() = Roster(
    confirmed = this.confirmed.map { it.toDomain() },
    waitlist = this.waitlist.map { it.toDomain() },
)