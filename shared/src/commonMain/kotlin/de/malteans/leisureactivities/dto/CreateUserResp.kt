package de.malteans.leisureactivities.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserResp(val user: UserDto, val token: String)