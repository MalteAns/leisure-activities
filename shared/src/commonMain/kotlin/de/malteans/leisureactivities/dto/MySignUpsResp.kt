package de.malteans.leisureactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class MySignUpsResp(
    val activityIds: Set<String>
)
