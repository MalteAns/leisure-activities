package de.malteans.sosactivities

import kotlinx.serialization.Serializable

@Serializable data class UserDto(val id: String, val name: String, val isStaff: Boolean, val isAdmin: Boolean)
@Serializable data class CreateUserReq(val id: String, val firstName: String, val lastName: String)
@Serializable data class CreateUserResp(val user: UserDto, val token: String)
@Serializable data class UpdateUserReq(val firstName: String, val lastName: String)

@Serializable data class SetStaffReq(val userId: String, val setStuff: Boolean)
@Serializable data class SetStaffResp(val user: UserDto)

@Serializable data class UpdateTokenResp(val user: UserDto, val token: String)

@Serializable data class CreateActivityReq(
    val title: String,
    val description: String? = null,
    val startsAt: String,       // ISO-8601 UTC; parse server-side to Instant
    val durationMin: Int,
    val imageId: String? = null
)
@Serializable data class ActivityDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val startsAt: String,
    val durationMin: Int,
    val imageId: String? = null
)

@Serializable data class RegistrationDto(val userId: String, val name: String)
@Serializable data class RegistrationResp(val status: String)
@Serializable data class RosterDto(
    val confirmed: List<RegistrationDto>,
    val waitlist: List<RegistrationDto> = emptyList()
)

@Serializable data class PresignReq(val filename: String, val mimeType: String, val size: Int)
@Serializable data class PresignResp(val id: String, val putUrl: String, val key: String)
@Serializable data class FinalizeResp(val id: String, val publicUrl: String, val width: Int?, val height: Int?)
