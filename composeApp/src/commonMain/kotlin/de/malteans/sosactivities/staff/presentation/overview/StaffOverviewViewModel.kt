package de.malteans.sosactivities.staff.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.sosactivities.model.ActivityWithImageUrl
import de.malteans.sosactivities.staff.domain.StaffService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StaffOverviewViewModel(
    private val staffService: StaffService
): ViewModel() {

    private val _allActivities = MutableStateFlow(emptyList<ActivityWithImageUrl>())
    private val _searchQuery = MutableStateFlow("")
    private val _state = MutableStateFlow(StaffOverviewState())

    val state = combine(
        _state,
        _allActivities,
        _searchQuery
    ) { state, allActivities, searchQuery ->
        state.copy(
            activitiesToShow = allActivities.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            },
            searchQuery = searchQuery,
        )

    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StaffOverviewState()
        )

    fun onAction(action: StaffOverviewAction) {
        when(action) {
            StaffOverviewAction.ClearError -> _state.update { it.copy(
                loadingActivitiesError = null,
                deletingActivitiesError = null
            ) }
            StaffOverviewAction.RefreshActivities -> viewModelScope.launch(Dispatchers.IO) {
                refreshActivities()
            }
            is StaffOverviewAction.OnSearchQueryChange -> _searchQuery.update { action.newValue }
            is StaffOverviewAction.OnDeletingActivitiesChange -> _state.update { state ->
                state.copy(
                    deletingActivities = action.deletingActivities,
                    activityIdsToDelete = if (action.deletingActivities) state.activityIdsToDelete else emptyList(),
                )
            }
            is StaffOverviewAction.OnSelectActivityToDelete -> _state.update { state ->
                state.copy(
                    deletingActivities = true,
                    activityIdsToDelete = state.activityIdsToDelete + action.activityId,
                )
            }
            is StaffOverviewAction.OnDeselectActivityToDelete -> _state.update { state ->
                val newActivityIdsToDelete = state.activityIdsToDelete - action.activityId
                state.copy(
                    deletingActivities = newActivityIdsToDelete.isNotEmpty(),
                    activityIdsToDelete = newActivityIdsToDelete,
                )
            }
            StaffOverviewAction.OnSubmitDelete -> viewModelScope.launch(Dispatchers.IO) {
                _state.update { it.copy(deletingActivitiesInProgress = true) }
                staffService.deleteActivities(state.value.activityIdsToDelete)
                    .onSuccess {
                        refreshActivities()
                        _state.update { it.copy(
                            deletingActivitiesInProgress = false,
                            deletingActivities = false,
                            activityIdsToDelete = emptyList(),
                            deletingActivitiesError = null,
                        ) }
                    }
                    .onFailure { error ->
                        _state.update { it.copy(
                            deletingActivitiesInProgress = false,
                            deletingActivitiesError = error,
                        ) }
                    }
            }
            else -> throw NotImplementedError("Action '$action' not implemented in StaffViewModel")
        }
    }

    suspend fun refreshActivities() {
        _state.update { it.copy(loadingActivities = true) }
        staffService.getAllActivities()
            .onSuccess { allActivities ->
                _allActivities.update { allActivities }
                _state.update { it.copy(
                    loadingActivities = false,
                    loadingActivitiesError = null,
                ) }
            }
            .onFailure { error ->
                _state.update { it.copy(
                    loadingActivities = false,
                    loadingActivitiesError = error,
                ) }
            }
    }
}