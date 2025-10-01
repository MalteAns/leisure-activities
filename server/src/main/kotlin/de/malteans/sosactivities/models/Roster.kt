package de.malteans.sosactivities.models

data class Roster(
    val confirmed: List<RegItem>,
    val waitlist: List<RegItem>
)