package de.malteans.leisureactivities.signUp.presentation

import de.malteans.leisureactivities.model.ActivityWithImageUrl

data class SignUpState(
    val ttsEnabled: Boolean = false,

    val loadingActivities: Boolean = true,
    val loadingActivitiesError: Throwable? = null,

    val searchQuery: String = "",

    val currentActivities: List<ActivityWithImageUrl> = emptyList(),

    val signedUpActivityIds: List<String> = emptyList(),
)