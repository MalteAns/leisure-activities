package de.malteans.leisureactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateActivityReq(
    val title: String,
    val startsAt: String,       // ISO-8601 UTC; parse server-side to Instant
    val endsAt: String?,        // ISO-8601 UTC; parse server-side to Instant
    val meetUpInformation: String,
    val activityLocation: String,
    val hostInformation: String,
    val contactPersonInformation: String,
    val imageId: String? = null
)