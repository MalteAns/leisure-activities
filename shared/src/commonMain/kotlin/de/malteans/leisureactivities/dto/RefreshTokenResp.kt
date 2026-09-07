package de.malteans.leisureactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResp(val user: UserDto, val token: String)