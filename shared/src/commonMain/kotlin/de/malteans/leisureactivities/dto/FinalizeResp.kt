package de.malteans.leisureactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class FinalizeResp(val id: String, val publicUrl: String, val width: Int?, val height: Int?)