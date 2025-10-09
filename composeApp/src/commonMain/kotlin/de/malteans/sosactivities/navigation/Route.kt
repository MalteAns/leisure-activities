package de.malteans.sosactivities.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object MainNav : Route
    @Serializable
    sealed interface Main : Route {
        @Serializable
        data object Register : Main
        @Serializable
        data object Settings : Main
    }
    @Serializable
    data object SignUpNav : Route
    @Serializable
    sealed interface SignUp : Route {
        @Serializable
        data object Overview : SignUp
    }
    @Serializable
    data object StaffNav : Route
    @Serializable
    sealed interface Staff : Route {
        @Serializable
        data object Overview : Staff
        @Serializable
        data class ActivityDetails(val activityId: String) : Staff
        @Serializable
        data class ModifyActivity(val activityId: String?) : Staff
    }
}