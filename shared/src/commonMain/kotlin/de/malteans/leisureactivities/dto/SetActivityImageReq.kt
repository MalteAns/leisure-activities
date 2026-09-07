package de.malteans.leisureactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class SetActivityImageReq(
    val imageId: String,
)
