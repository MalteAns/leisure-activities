package de.malteans.sosactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class SetActivityImageReq(
    val imageId: String,
)
