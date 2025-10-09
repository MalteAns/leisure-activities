package de.malteans.sosactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class ActivityDto(
    val id: String,
    val title: String,
    val startsAt: String,
    val endsAt: String?,
    val meetUpInformation: String,
    val activityLocation: String,
    val hostInformation: String,
    val contactPersonInformation: String,
    val imageId: String? = null,
    val imageUrl: String? = null,
)