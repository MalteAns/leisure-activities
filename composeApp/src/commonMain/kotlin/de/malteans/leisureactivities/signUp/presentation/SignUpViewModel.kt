package de.malteans.leisureactivities.signUp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.leisureactivities.core.domain.DataStoreRepository
import de.malteans.leisureactivities.core.presentation.util.TextToSpeechService
import de.malteans.leisureactivities.model.ActivityWithImageUrl
import de.malteans.leisureactivities.signUp.domain.SignUpService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SignUpViewModel(
    private val signUpService: SignUpService,
    private val dataStoreRepository: DataStoreRepository,
    private val ttsService: TextToSpeechService,
): ViewModel() {

    private val _ttsEnabled = dataStoreRepository.getTtsEnabled()
    private val _currentActivities =  MutableStateFlow<List<ActivityWithImageUrl>>(emptyList())
    private val _signUpStates =  MutableStateFlow<Map<String, Boolean?>>(emptyMap())

    private val _state = MutableStateFlow(SignUpState(ttsEnabled = _ttsEnabled ?: false))

    val state = combine(
        _state,
        _currentActivities,
        _signUpStates,
    ) { state, currentActivities, signUpStates ->
        state.copy(
            currentActivities = currentActivities.filter {
                it.title.contains(state.searchQuery, true)
            }.map {
                it.copy(signedUp = signUpStates[it.id])
            },
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SignUpState(ttsEnabled = _ttsEnabled ?: false)
        )

    fun onOverviewAction(action: SignUpOverviewAction) {
        when (action) {
            SignUpOverviewAction.ClearError -> {
                _state.update { it.copy(loadingActivitiesError = null) }
            }
            is SignUpOverviewAction.OnTTS -> {
                if (_ttsEnabled == true) {
                    viewModelScope.launch(Dispatchers.IO) {
                        ttsService.speak(action.uiTexts.map { it.asStringAsync() }.joinToString("\n"))
                    }
                }
            }
            SignUpOverviewAction.RefreshCurrentActivities -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(loadingActivities = true) }
                    signUpService.getCurrentActivities()
                        .onSuccess { activities ->
                            _currentActivities.update { activities }
                            signUpService.getSignedUpActivityIds()
                                .onSuccess { activityIds ->
                                    _signUpStates.update {
                                        activities.associate { activity ->
                                            activity.id to (activity.id in activityIds)
                                        }
                                    }
                                    _state.update { it.copy(
                                        loadingActivitiesError = null,
                                        loadingActivities = false
                                    ) }
                                }
                                .onFailure { error ->
                                    _state.update { it.copy(
                                        loadingActivitiesError = error,
                                        loadingActivities = false,
                                    ) }
                                }
                        }
                        .onFailure { error ->
                            _state.update { it.copy(
                                loadingActivitiesError = error,
                                loadingActivities = false,
                            ) }
                        }
                }
            }
            is SignUpOverviewAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.newValue) }
            }
            is SignUpOverviewAction.SignUpForActivity -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _signUpStates.update { current ->
                        val mutableMap = current.toMutableMap()
                        mutableMap[action.activityId] = null // null means loading
                        mutableMap
                    }
                    signUpService.signUpForActivity(action.activityId)
                        .onSuccess {
                            // TODO: React on Status
                            _signUpStates.update { current ->
                                val mutableMap = current.toMutableMap()
                                mutableMap[action.activityId] = true
                                mutableMap
                            }
                        }
                        .onFailure {
                            // TODO: Show Error
                        }
                }
            }
            is SignUpOverviewAction.SignOutFromActivity -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _signUpStates.update { current ->
                        val mutableMap = current.toMutableMap()
                        mutableMap[action.activityId] = null // null means loading
                        mutableMap
                    }
                    signUpService.signOutFromActivity(action.activityId)
                        .onSuccess {
                            // TODO: React on Status
                            _signUpStates.update { current ->
                                val mutableMap = current.toMutableMap()
                                mutableMap[action.activityId] = false
                                mutableMap
                            }
                        }
                        .onFailure {
                            // TODO: Show Error
                        }
                }
            }
            else -> throw NotImplementedError("Action $action is not implemented in RegistrationViewModel")
        }
    }

}