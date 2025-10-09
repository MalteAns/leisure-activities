package de.malteans.sosactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserReq(val firstName: String, val lastName: String)