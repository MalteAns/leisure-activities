package de.malteans.sosactivities.staff.domain

import de.malteans.sosactivities.model.ActivityWithImageUrl
import de.malteans.sosactivities.model.Image
import de.malteans.sosactivities.model.Roster
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
interface StaffService {
    suspend fun getActivity(activityId: String): Result<ActivityWithImageUrl>
    suspend fun getAllActivities(): Result<List<ActivityWithImageUrl>>

    suspend fun createActivity(
        title: String,
        startsAt: Instant,
        endsAt: Instant?,
        meetUpInformation: String,
        activityLocation: String,
        hostInformation: String,
        contactPersonInformation: String,
        imageId: String?,
    ): Result<ActivityWithImageUrl>

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
    ): Result<ActivityWithImageUrl>

    suspend fun deleteActivity(activityId: String): Result<Unit>

    suspend fun getRoster(activityId: String): Result<Roster>

    suspend fun getAllImages(): Result<List<Image>>

    /** @return Result of image url */
    suspend fun uploadImage(imageBytes: ByteArray, fileName: String): Result<String>

    suspend fun deleteImage(imageId: String): Result<Unit>
}