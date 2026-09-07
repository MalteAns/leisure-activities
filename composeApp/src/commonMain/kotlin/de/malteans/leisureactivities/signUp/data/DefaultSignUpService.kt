package de.malteans.leisureactivities.signUp.data

import de.malteans.leisureactivities.Constants
import de.malteans.leisureactivities.core.domain.RemoteService
import de.malteans.leisureactivities.model.ActivityWithImageUrl
import de.malteans.leisureactivities.model.RegStatus
import de.malteans.leisureactivities.model.ext.toDomainWithImageUrl
import de.malteans.leisureactivities.signUp.domain.SignUpService
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