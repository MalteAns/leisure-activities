package de.malteans.sosactivities.staff.data

import de.malteans.sosactivities.Constants
import de.malteans.sosactivities.core.domain.RemoteService
import de.malteans.sosactivities.model.ActivityWithImageUrl
import de.malteans.sosactivities.model.Image
import de.malteans.sosactivities.model.Roster
import de.malteans.sosactivities.model.ext.toDomain
import de.malteans.sosactivities.model.ext.toDomainWithImageUrl
import de.malteans.sosactivities.staff.domain.StaffService
import io.ktor.http.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class DefaultStaffService(
    private val remoteService: RemoteService,
): StaffService {
    override suspend fun getActivity(activityId: String): Result<ActivityWithImageUrl> {
        return remoteService.getActivity(activityId)
            .map { it.toDomainWithImageUrl(Constants.BASE_URL) }
    }

    override suspend fun getAllActivities(): Result<List<ActivityWithImageUrl>> {
        return remoteService.getAllActivities(null, null)
            .map { list -> list.map {
                it.toDomainWithImageUrl(Constants.BASE_URL)
            } }
    }

    override suspend fun createActivity(
        title: String,
        startsAt: Instant,
        endsAt: Instant?,
        meetUpInformation: String,
        activityLocation: String,
        hostInformation: String,
        contactPersonInformation: String,
        imageId: String?
    ): Result<ActivityWithImageUrl> {
        return remoteService.createActivity(
            title = title,
            startsAt = startsAt,
            endsAt = endsAt,
            meetUpInformation = meetUpInformation,
            activityLocation = activityLocation,
            hostInformation = hostInformation,
            contactPersonInformation = contactPersonInformation,
            imageId = imageId
        ).map { it.toDomainWithImageUrl(Constants.BASE_URL) }
    }

    override suspend fun updateActivity(
        activityId: String,
        title: String,
        startsAt: Instant,
        endsAt: Instant?,
        meetUpInformation: String,
        activityLocation: String,
        hostInformation: String,
        contactPersonInformation: String,
        imageId: String?
    ): Result<ActivityWithImageUrl> {
        return remoteService.updateActivity(
            activityId = activityId,
            title = title,
            startsAt = startsAt,
            endsAt = endsAt,
            meetUpInformation = meetUpInformation,
            activityLocation = activityLocation,
            hostInformation = hostInformation,
            contactPersonInformation = contactPersonInformation,
            imageId = imageId
        ).map { it.toDomainWithImageUrl(Constants.BASE_URL) }
    }

    override suspend fun deleteActivities(activityIds: List<String>): Result<Unit> {
        return remoteService.deleteActivities(activityIds)
    }

    override suspend fun getRoster(activityId: String): Result<Roster> {
        return remoteService.getRoster(activityId)
            .map { it.toDomain() }
    }

    override suspend fun getAllImages(): Result<List<Image>> {
        return remoteService.getAllImages()
            .map { list -> list.map {
                it.toDomain()
            } }
    }

    override suspend fun uploadImage(
        filename: String,
        imageBytes: ByteArray,
        mimeType: String,
    ): Result<Image> {
        return remoteService.uploadImage(
            fileName = filename,
            mimeType = ContentType.parse(mimeType),
            imageBytes = imageBytes,
        )
    }

    override suspend fun deleteImage(imageId: String): Result<Unit> {
        return remoteService.deleteImage(imageId)
    }
}