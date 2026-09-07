package de.malteans.leisureactivities.registration.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.leisureactivities.registration.domain.RegistrationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val registrationService: RegistrationService,
): ViewModel() {

    private val _state = MutableStateFlow(RegistrationState())

    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RegistrationState()
        )

    fun onAction(action: RegistrationAction) {
        when (action) {
            is RegistrationAction.ResetError -> _state.update { it.copy(error = null) }
            is RegistrationAction.OnTokenChange -> {
                _state.update { it.copy(
                    registrationToken = action.token,
                    validToken = false,
                ) }
                if (action.token.length == 128 && action.token.all { it.isLetterOrDigit() }) {
                    viewModelScope.launch(Dispatchers.IO) {
                        _state.update { it.copy(validToken = null) }
                        registrationService.checkToken(action.token)
                            .onSuccess { result ->
                                _state.update { it.copy(validToken = result) }
                            }
                            .onFailure { error ->
                                _state.update { it.copy(
                                    error = error,
                                    validToken = false,
                                ) }
                            }
                    }
                }
            }
            is RegistrationAction.OnFirstNameChange -> _state.update { it.copy(firstName = action.firstName) }
            is RegistrationAction.OnLastNameChange -> _state.update { it.copy(lastName = action.lastName) }
            is RegistrationAction.Submit -> viewModelScope.launch(Dispatchers.IO) {
                _state.update { it.copy(isLoading = true) }
                registrationService.createUser(
                    registrationToken = state.value.registrationToken,
                    firstName = state.value.firstName,
                    lastName = state.value.lastName
                )
                    .onSuccess { user ->
                        _state.update { it.copy(createdUser = user) }
                    }
                    .onFailure { error ->
                        _state.update { it.copy(error = error, isLoading = false) }
                    }
            }
            else -> throw NotImplementedError("Action '$action' is not implemented in RegistrationViewModel")
        }
    }
}