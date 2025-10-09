package de.malteans.sosactivities.core.domain

interface MainService {

    fun isUserCreated(): Boolean
    fun getUserIsStaff(): Boolean

    suspend fun refreshToken(): Result<Boolean>
}