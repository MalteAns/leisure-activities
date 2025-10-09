package de.malteans.sosactivities.dto

import de.malteans.sosactivities.model.RegStatus
import kotlinx.serialization.Serializable

@Serializable
data class SignUpResp(val status: RegStatus)