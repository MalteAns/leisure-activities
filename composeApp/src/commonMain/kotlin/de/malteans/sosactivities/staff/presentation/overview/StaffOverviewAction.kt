package de.malteans.sosactivities.staff.presentation.overview

sealed interface StaffOverviewAction {
    data class ShowDrawer(val show: Boolean) : StaffOverviewAction

    data object OnCreateActivity : StaffOverviewAction
    data class OnEditActivity(val activityId: String) : StaffOverviewAction
    data class OnShowActivity(val activityId: String) : StaffOverviewAction
    data class OnDeletingActivitiesChange(val deletingActivities: Boolean) : StaffOverviewAction
    data class OnSelectActivityToDelete(val activityId: String) : StaffOverviewAction
    data class OnDeselectActivityToDelete(val activityId: String) : StaffOverviewAction
    data object OnSubmitDelete : StaffOverviewAction

    data object ClearError : StaffOverviewAction
    data object RefreshActivities : StaffOverviewAction

    data class OnSearchQueryChange(val newValue: String) : StaffOverviewAction
}