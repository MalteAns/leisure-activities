package de.malteans.leisureactivities.registration.domain

import de.malteans.leisureactivities.model.User

interface RegistrationService {

    suspend fun createUser(
        registrationToken: String,
        firstName: String,
        lastName: String
    ): Result<User>

    suspend fun checkToken(
        token: String
    ): Result<Boolean>
}