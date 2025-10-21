package de.malteans.sosactivities.core.presentation.settings

import de.malteans.legal.presentation.navigation.LegalRoute

sealed interface SettingsAction {
    data class ShowDrawer(val show: Boolean) : SettingsAction
    data class NavigateToLegalScreen(val route: LegalRoute) : SettingsAction

    data class TtsEnabledChange(val enabled: Boolean) : SettingsAction
}