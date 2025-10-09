package de.malteans.sosactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class MySignUpsResp(
    val activityIds: Set<String>
)
