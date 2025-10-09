package de.malteans.sosactivities.staff.presentation.activityDetails

sealed interface ActivityDetailsAction {
    data object OnNavigateBack : ActivityDetailsAction
    data object ClearError : ActivityDetailsAction

    data class OnTabSelected(val index: Int) : ActivityDetailsAction

    data object RefreshActivityDetails : ActivityDetailsAction
    data object RefreshParticipants : ActivityDetailsAction
}