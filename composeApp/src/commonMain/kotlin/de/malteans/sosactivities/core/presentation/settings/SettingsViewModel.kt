package de.malteans.sosactivities.core.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.sosactivities.core.domain.DataStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    val dataStoreRepository: DataStoreRepository
): ViewModel() {

    private val _ttsEnabled = dataStoreRepository.getTtsEnabledFlow()
    private val _state = MutableStateFlow(SettingsState())

    val state = combine(
        _state,
        _ttsEnabled
    ) { state, ttsEnabled ->
        state.copy(
            ttsEnabled = ttsEnabled ?: false
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            SettingsState()
        )

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.TtsEnabledChange -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dataStoreRepository.setTtsEnabled(action.enabled)
                }
            }
            else -> throw NotImplementedError("Action '$action' is not implemented in SettingsViewModel")
        }
    }
}