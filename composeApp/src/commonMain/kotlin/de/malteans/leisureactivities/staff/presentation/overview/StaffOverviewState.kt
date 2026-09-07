package de.malteans.leisureactivities.staff.presentation.overview

import de.malteans.leisureactivities.model.ActivityWithImageUrl

data class StaffOverviewState(
    val searchQuery: String = "",

    val loadingActivities: Boolean = true,
    val loadingActivitiesError: Throwable? = null,

    val activitiesToShow: List<ActivityWithImageUrl> = emptyList(),

    val deletingActivities: Boolean = false,
    val activityIdsToDelete: List<String> = emptyList(),
    val deletingActivitiesInProgress: Boolean = false,
    val deletingActivitiesError: Throwable? = null,
)
