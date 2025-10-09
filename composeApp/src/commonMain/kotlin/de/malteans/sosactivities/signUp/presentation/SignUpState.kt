package de.malteans.sosactivities.signUp.presentation

import de.malteans.sosactivities.model.ActivityWithImageUrl

data class SignUpState(
    val ttsEnabled: Boolean = false,

    val loadingActivities: Boolean = true,
    val loadingActivitiesError: Throwable? = null,

    val searchQuery: String = "",

    val currentActivities: List<ActivityWithImageUrl> = emptyList(),

    val signedUpActivityIds: List<String> = emptyList(),
)