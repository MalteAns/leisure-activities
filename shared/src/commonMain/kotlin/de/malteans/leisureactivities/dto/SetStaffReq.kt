package de.malteans.leisureactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class SetStaffReq(val userId: String, val setStuff: Boolean)