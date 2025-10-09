package de.malteans.sosactivities.signUp.presentation

import de.malteans.sosactivities.core.presentation.util.UiText

sealed interface SignUpOverviewAction {
    data class ShowDrawer(val show: Boolean) : SignUpOverviewAction
    data object ClearError : SignUpOverviewAction

    data class OnTTS(val uiTexts: List<UiText>) : SignUpOverviewAction
    data object RefreshCurrentActivities : SignUpOverviewAction
    data class OnSearchQueryChange(val newValue: String) : SignUpOverviewAction

    data class SignUpForActivity(val activityId: String) : SignUpOverviewAction
    data class SignOutFromActivity(val activityId: String) : SignUpOverviewAction
}