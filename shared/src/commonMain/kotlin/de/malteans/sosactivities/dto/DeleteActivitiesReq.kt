package de.malteans.sosactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeleteActivitiesReq(
    val activityIds: List<String>
)
