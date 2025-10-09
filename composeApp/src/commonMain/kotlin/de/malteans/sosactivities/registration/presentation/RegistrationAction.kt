package de.malteans.sosactivities.registration.presentation

sealed interface RegistrationAction {
    data object ResetError : RegistrationAction

    data class OnTokenChange(val token: String) : RegistrationAction
    data class OnFirstNameChange(val firstName: String) : RegistrationAction
    data class OnLastNameChange(val lastName: String) : RegistrationAction

    data object Submit : RegistrationAction

    data object OnUserCreated : RegistrationAction
}