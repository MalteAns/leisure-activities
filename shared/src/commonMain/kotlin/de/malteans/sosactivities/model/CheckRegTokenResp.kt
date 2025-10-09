package de.malteans.sosactivities.model

import kotlinx.serialization.Serializable

@Serializable
data class CheckRegTokenResp(
    val valid: Boolean
)