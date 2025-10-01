package de.malteans.sosactivities.models

import java.time.Instant

data class Activity(
    val id: String,
    val title: String,
    val description: String?,
    val startsAt: Instant,
    val durationMin: Int,
    val imageId: String?
)