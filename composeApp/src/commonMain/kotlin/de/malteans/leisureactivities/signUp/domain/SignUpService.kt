package de.malteans.leisureactivities.signUp.domain

import de.malteans.leisureactivities.model.ActivityWithImageUrl
import de.malteans.leisureactivities.model.RegStatus

interface SignUpService {

    suspend fun getCurrentActivities(): Result<List<ActivityWithImageUrl>>

    suspend fun getSignedUpActivityIds(): Result<Set<String>>

    suspend fun signUpForActivity(activityId: String): Result<RegStatus>

    suspend fun signOutFromActivity(activityId: String): Result<Unit>
}