package de.malteans.sosactivities.model

data class Roster(
    val confirmed: List<Participant>,
    val waitlist: List<Participant>
)