package de.malteans.sosactivities.core.presentation.settings

sealed interface SettingsAction {
    data class ShowDrawer(val show: Boolean) : SettingsAction

    data class TtsEnabledChange(val enabled: Boolean) : SettingsAction
}