package de.malteans.leisureactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserReq(val firstName: String, val lastName: String)