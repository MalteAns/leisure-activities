package de.malteans.leisureactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeleteActivitiesReq(
    val activityIds: List<String>
)
