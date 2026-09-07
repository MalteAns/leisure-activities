package de.malteans.leisureactivities.staff.presentation.activityDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.leisureactivities.staff.domain.StaffService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ActivityDetailsViewModel(
    private val staffService: StaffService,
): ViewModel() {

    private val _state = MutableStateFlow(ActivityDetailsState())

    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ActivityDetailsState(),
        )

    fun setActivityId(activityId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(loadingActivity = true) }
            if (activityId == null) _state.update { it.copy(
                currentActivity = null,
                loadingActivity = true,
            ) }
            else staffService.getActivity(activityId)
                .onSuccess { activity ->
                    refreshParticipants(activityId)
                    _state.update { it.copy(
                        currentActivity = activity,
                        loadingActivity = false,
                        loadingActivityError = null,
                    ) }
                }
                .onFailure { error ->
                    _state.update { it.copy(
                        loadingActivity = true,
                        loadingActivityError = error,
                    ) }
                }
        }
    }

    fun onAction(action: ActivityDetailsAction) {
        when (action) {
            ActivityDetailsAction.ClearError -> _state.update { it.copy(loadingActivityError = null) }
            is ActivityDetailsAction.OnTabSelected -> _state.update { it.copy(selectedTabIndex = action.index) }
            ActivityDetailsAction.RefreshActivityDetails -> setActivityId(state.value.currentActivity?.id)
            ActivityDetailsAction.RefreshParticipants -> viewModelScope.launch(Dispatchers.IO) {
                state.value.currentActivity?.id?.let { refreshParticipants(it) }
            }
            else -> throw NotImplementedError("Action '$action' not implemented in ActivityDetailsViewModel")
        }
    }

    private suspend fun refreshParticipants(activityId: String) {
        _state.update { it.copy(loadingParticipants = true) }
        staffService.getRoster(activityId)
            .onSuccess { roster ->
                _state.update { it.copy(
                    participants = roster.confirmed, // TODO: Implement waitlist
                    loadingParticipants = false,
                    loadingParticipantsError = null,
                ) }
            }
            .onFailure { error ->
                _state.update { it.copy(
                    loadingParticipants = true,
                    loadingParticipantsError = error,
                ) }
            }
    }
}