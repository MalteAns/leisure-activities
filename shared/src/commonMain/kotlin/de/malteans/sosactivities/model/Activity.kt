package de.malteans.sosactivities.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Activity(
    val id: String,
    val title: String,
    val startsAt: Instant,
    val endsAt: Instant?,
    val meetUpInformation: String,
    val activityLocation: String,
    val hostInformation: String,
    val contactPersonInformation: String,
    val imageId: String?
)