package de.malteans.leisureactivities.core.domain

import de.malteans.leisureactivities.dto.*
import de.malteans.leisureactivities.model.CheckRegTokenResp
import de.malteans.leisureactivities.model.Image
import io.ktor.http.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
interface RemoteService {
    // Registration operations
    suspend fun checkRegToken(
        token: String
    ): Result<CheckRegTokenResp>

    suspend fun createUser(
        registrationToken: String,
        firstName: String,
        lastName: String,
    ): Result<CreateUserResp>

    // JWT Token operations
    suspend fun refreshToken(): Result<RefreshTokenResp>

    // Activity operations
    suspend fun getActivity(
        activityId: String
    ): Result<ActivityDto>

    suspend fun getAllActivities(
        from: Instant? = Clock.System.now(),
        to: Instant? = null
    ): Result<List<ActivityDto>>

    suspend fun getSignedUpActivityIds(): Result<MySignUpsResp>

    suspend fun createActivity(
        title: String,
        startsAt: Instant,
        endsAt: Instant?,
        meetUpInformation: String,
        activityLocation: String,
        hostInformation: String,
        contactPersonInformation: String,
        imageId: String?,
    ): Result<ActivityDto>

    suspend fun updateActivity(
        activityId: String,
        title: String,
        startsAt: Instant,
        endsAt: Instant?,
        meetUpInformation: String,
        activityLocation: String,
        hostInformation: String,
        contactPersonInformation: String,
        imageId: String?,
    ): Result<ActivityDto>

    suspend fun deleteActivity(
        activityId: String
    ): Result<Unit>

    suspend fun deleteActivities(
        activityIds: List<String>
    ): Result<Unit>

    suspend fun getRoster(
        activityId: String
    ): Result<RosterDto>

    // Sign Up operations
    suspend fun signUpForActivity(
        activityId: String
    ): Result<SignUpResp>

    suspend fun signOutFromActivity(
        activityId: String
    ): Result<Unit>

    // Image operations
    suspend fun getAllImages(): Result<List<ImageDto>>

    /** @return Result of image url */
    suspend fun uploadImage(
        fileName: String,
        mimeType: ContentType,
        imageBytes: ByteArray,
    ): Result<Image>

    suspend fun deleteImage(
        imageId: String
    ): Result<Unit>
}