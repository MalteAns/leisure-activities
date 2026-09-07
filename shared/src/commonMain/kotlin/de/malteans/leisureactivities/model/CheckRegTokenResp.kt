package de.malteans.leisureactivities.model

import kotlinx.serialization.Serializable

@Serializable
data class CheckRegTokenResp(
    val valid: Boolean
)