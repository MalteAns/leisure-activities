package de.malteans.leisureactivities.core.domain

import de.malteans.leisureactivities.model.User
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    fun isUserCreated(): Boolean
    fun getUserFlow(): Flow<User?>
    suspend fun saveUser(user: User)
    fun getUserIsStaff(): Boolean

    fun getToken(): String?
    suspend fun saveToken(token: String)

    fun getTtsEnabled(): Boolean?
    fun getTtsEnabledFlow(): Flow<Boolean?>
    suspend fun setTtsEnabled(enabled: Boolean)
}