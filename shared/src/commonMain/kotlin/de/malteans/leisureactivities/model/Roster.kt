package de.malteans.leisureactivities.model

data class Roster(
    val confirmed: List<Participant>,
    val waitlist: List<Participant>
)