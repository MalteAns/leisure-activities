package de.malteans.sosactivities.core.data

import de.malteans.sosactivities.Endpoints
import de.malteans.sosactivities.core.data.network.safeCall
import de.malteans.sosactivities.core.domain.DataStoreRepository
import de.malteans.sosactivities.core.domain.RemoteService
import de.malteans.sosactivities.dto.*
import de.malteans.sosactivities.model.CheckRegTokenResp
import de.malteans.sosactivities.model.Image
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class DefaultRemoteService(
    private val client: HttpClient,
    private val dataStoreRepository: DataStoreRepository,
): RemoteService {
    private val json = Json {
        classDiscriminator = "type"
        ignoreUnknownKeys = true
    }

    override suspend fun checkRegToken(
        token: String
    ) = safeCall<CheckRegTokenResp> {
        client.get(Endpoints.RegTokenCheck(token).url)
    }

    override suspend fun createUser(
        registrationToken: String,
        firstName: String,
        lastName: String
    ) = safeCall<CreateUserResp> {
        client.post(Endpoints.CreateUser.url) {
            contentType(ContentType.Application.Json)
            setBody(CreateUserReq(registrationToken, firstName, lastName))
        }
    }

    override suspend fun refreshToken()= safeCall<RefreshTokenResp> {
        client.get(Endpoints.JwkTokenRefresh.url) {
            header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
        }
    }

    override suspend fun getActivity(
        activityId: String
    ) = safeCall<ActivityDto> {
        client.get(Endpoints.Activity(activityId).url) {
            header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
        }
    }

    override suspend fun getAllActivities(
        from: Instant?,
        to: Instant?,
    ) = safeCall<List<ActivityDto>> {
        client.get(Endpoints.AllActivities.url) {
            header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
            from?.let { parameter("from", it.toString()) }
            to?.let { parameter("to", it.toString()) }
        }
    }

    override suspend fun getSignedUpActivityIds() = safeCall<MySignUpsResp> {
        client.get(Endpoints.UserSignUps.url) {
            header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
        }
    }

    override suspend fun createActivity(
        title: String,
        startsAt: Instant,
        endsAt: Instant?,
        meetUpInformation: String,
        activityLocation: String,
        hostInformation: String,
        contactPersonInformation: String,
        imageId: String?,
    ) = safeCall<ActivityDto> {
        client.post(Endpoints.AllActivities.url) {
            header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
            contentType(ContentType.Application.Json)
            setBody(CreateActivityReq(
                title = title,
                startsAt = startsAt.toString(),
                endsAt = endsAt?.toString(),
                meetUpInformation = meetUpInformation,
                activityLocation = activityLocation,
                hostInformation = hostInformation,
                contactPersonInformation = contactPersonInformation,
                imageId = imageId,
            ))
        }
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
    ) = safeCall<ActivityDto> {
        client.patch(Endpoints.Activity(activityId).url) {
            header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
            contentType(ContentType.Application.Json)
            setBody(UpdateActivityReq(
                title = title,
                startsAt = startsAt.toString(),
                endsAt = endsAt?.toString(),
                meetUpInformation = meetUpInformation,
                activityLocation = activityLocation,
                hostInformation = hostInformation,
                contactPersonInformation = contactPersonInformation,
                imageId = imageId,
            ))
        }
    }

    override suspend fun deleteActivity(
        activityId: String
    ) = safeCall<Unit> {
        client.delete(Endpoints.Activity(activityId).url) {
            header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
        }
    }

    override suspend fun getRoster(
        activityId: String
    ) = safeCall<RosterDto> {
        client.get(Endpoints.ActivitySignUp(activityId).url) {
            header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
        }
    }

    override suspend fun signUpForActivity(
        activityId: String
    ) = safeCall<SignUpResp> {
        client.post(Endpoints.ActivitySignUp(activityId).url) {
            header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
        }
    }

    override suspend fun signOutFromActivity(
        activityId: String
    ) = safeCall<Unit> {
        client.delete(Endpoints.ActivitySignUp(activityId).url) {
            header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
        }
    }

    override suspend fun getAllImages() = safeCall<List<ImageDto>> {
        client.get(Endpoints.Images.url) {
            header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
        }
    }

    override suspend fun uploadImage(
        fileName: String,
        mimeType: ContentType,
        imageBytes: ByteArray,
    ): Result<Image> {
        val presignResp = safeCall<PresignResp> {
            client.post(Endpoints.ImagePresign.url) {
                header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
                contentType(ContentType.Application.Json)
                setBody(ImagePresignReq(
                    filename = fileName,
                    mimeType = mimeType.contentType,
                    size = imageBytes.size,
                ))
            }
        }.getOrElse { return Result.failure(it) }
        safeCall<Unit> {
            client.put(presignResp.putUrl) {
                header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
                contentType(mimeType)
                setBody(imageBytes)
            }
        }.onFailure { return Result.failure(it) }
        val finalizeResp = safeCall<FinalizeResp> {
            client.post(Endpoints.ImageFinalize(presignResp.id).url) {
                header("Authorization", "Bearer ${dataStoreRepository.getToken()}")
            }
        }.getOrElse { return Result.failure(it) }
        return Result.success(
            Image(
                id = presignResp.id,
                filename = fileName,
                publicUrl = finalizeResp.publicUrl,
                mimeType = mimeType.contentType,
                byteSize = imageBytes.size,
                width = finalizeResp.width,
                height = finalizeResp.height,
            )
        )
    }

    override suspend fun deleteImage(
        imageId: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }
}