package de.malteans.leisureactivities.staff.presentation.activityDetails

import de.malteans.leisureactivities.model.ActivityWithImageUrl
import de.malteans.leisureactivities.model.Participant

data class ActivityDetailsState(
    val loadingActivity: Boolean = true,
    val loadingParticipants: Boolean = true,
    val loadingActivityError: Throwable? = null,
    val loadingParticipantsError: Throwable? = null,

    val selectedTabIndex: Int = 0,

    val currentActivity: ActivityWithImageUrl? = null,
    val participants: List<Participant> = emptyList(),
)
