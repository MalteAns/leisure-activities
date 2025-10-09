package de.malteans.sosactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserResp(val user: UserDto, val token: String)