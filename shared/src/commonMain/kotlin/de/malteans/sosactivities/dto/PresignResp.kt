package de.malteans.sosactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class PresignResp(val id: String, val putUrl: String, val key: String)