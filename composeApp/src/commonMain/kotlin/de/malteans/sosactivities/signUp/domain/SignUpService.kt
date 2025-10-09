package de.malteans.sosactivities.signUp.domain

import de.malteans.sosactivities.model.ActivityWithImageUrl
import de.malteans.sosactivities.model.RegStatus

interface SignUpService {

    suspend fun getCurrentActivities(): Result<List<ActivityWithImageUrl>>

    suspend fun getSignedUpActivityIds(): Result<Set<String>>

    suspend fun signUpForActivity(activityId: String): Result<RegStatus>

    suspend fun signOutFromActivity(activityId: String): Result<Unit>
}