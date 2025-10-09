package de.malteans.sosactivities.staff.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.sosactivities.model.ActivityWithImageUrl
import de.malteans.sosactivities.staff.domain.StaffService
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

    fun onOverviewAction(action: StaffOverviewAction) {
        when(action) {
            StaffOverviewAction.ClearError -> _state.update { it.copy(loadingActivitiesError = null) }
            StaffOverviewAction.RefreshActivities -> viewModelScope.launch {
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
            is StaffOverviewAction.OnSearchQueryChange -> _searchQuery.update { action.newValue }
            else -> throw NotImplementedError("Action '$action' not implemented in StaffViewModel")
        }
    }
}