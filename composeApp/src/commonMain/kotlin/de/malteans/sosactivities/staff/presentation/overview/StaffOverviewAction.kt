package de.malteans.sosactivities.staff.presentation.overview

sealed interface StaffOverviewAction {
    data class ShowDrawer(val show: Boolean) : StaffOverviewAction

    data object OnCreateActivity : StaffOverviewAction
    data class OnModifyActivity(val activityId: String) : StaffOverviewAction
    data class OnShowActivity(val activityId: String) : StaffOverviewAction

    data object ClearError : StaffOverviewAction
    data object RefreshActivities : StaffOverviewAction

    data class OnSearchQueryChange(val newValue: String) : StaffOverviewAction
}