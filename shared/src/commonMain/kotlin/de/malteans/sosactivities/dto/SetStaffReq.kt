package de.malteans.sosactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class SetStaffReq(val userId: String, val setStuff: Boolean)