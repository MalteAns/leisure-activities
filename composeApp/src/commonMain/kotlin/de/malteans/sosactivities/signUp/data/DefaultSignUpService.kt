package de.malteans.sosactivities.signUp.data

import de.malteans.sosactivities.Constants
import de.malteans.sosactivities.core.domain.RemoteService
import de.malteans.sosactivities.model.ActivityWithImageUrl
import de.malteans.sosactivities.model.RegStatus
import de.malteans.sosactivities.model.ext.toDomainWithImageUrl
import de.malteans.sosactivities.signUp.domain.SignUpService
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class DefaultSignUpService(
    val remoteService: RemoteService,
): SignUpService {
    override suspend fun getCurrentActivities(): Result<List<ActivityWithImageUrl>> {
        return remoteService.getAllActivities()
            .map { list -> list.map {
                it.toDomainWithImageUrl(Constants.BASE_URL)
            } }
    }

    override suspend fun getSignedUpActivityIds(): Result<Set<String>> {
        return remoteService.getSignedUpActivityIds()
            .map { it.activityIds }
    }

    override suspend fun signUpForActivity(activityId: String): Result<RegStatus> {
        return remoteService.signUpForActivity(activityId)
            .map { it.status }
    }

    override suspend fun signOutFromActivity(activityId: String): Result<Unit> {
        return remoteService.signOutFromActivity(activityId)
    }
}