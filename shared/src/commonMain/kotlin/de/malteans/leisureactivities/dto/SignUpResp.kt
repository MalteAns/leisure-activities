package de.malteans.leisureactivities.dto

import de.malteans.leisureactivities.model.RegStatus
import kotlinx.serialization.Serializable

@Serializable
data class SignUpResp(val status: RegStatus)