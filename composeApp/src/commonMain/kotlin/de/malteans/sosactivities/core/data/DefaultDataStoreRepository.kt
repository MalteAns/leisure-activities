package de.malteans.sosactivities.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import de.malteans.sosactivities.core.domain.DataStoreRepository
import de.malteans.sosactivities.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class DefaultDataStoreRepository(
    private val dataStore: DataStore<Preferences>
): DataStoreRepository {
    private enum class StringKey(val prefKey: Preferences.Key<String>) {
        USER_ID(stringPreferencesKey("USER_ID")),
        USER_FIRST_NAME(stringPreferencesKey("USER_FIRST_NAME")),
        USER_LAST_NAME(stringPreferencesKey("USER_LAST_NAME")),
        TOKEN(stringPreferencesKey("JWT_TOKEN"))
    }private enum class BooleanKey(val prefKey: Preferences.Key<Boolean>) {
        USER_IS_STAFF(booleanPreferencesKey("USER_IS_STAFF")),
        USER_IS_ADMIN(booleanPreferencesKey("USER_IS_ADMIN")),
        TTS_ENABLED(booleanPreferencesKey("TTS_ENABLED")),
    }


    private fun getString(key: StringKey): String? = runBlocking {
        dataStore.data.firstOrNull()?.get(key.prefKey)
    }
    private fun getStringFlow(key: StringKey, default: String? = null): Flow<String?> =
        dataStore.data.map { it[key.prefKey] ?: default }
    private suspend fun saveString(key: StringKey, value: String?) {
        dataStore.edit { prefs ->
            if (value == null) prefs.remove(key.prefKey)
            else prefs[key.prefKey] = value
        }
    }

    private fun getBoolean(key: BooleanKey): Boolean? = runBlocking {
        dataStore.data.firstOrNull()?.get(key.prefKey)
    }
    private fun getBooleanFlow(key: BooleanKey, default: Boolean? = null): Flow<Boolean?> =
        dataStore.data.map { it[key.prefKey] ?: default }
    private suspend fun saveBoolean(key: BooleanKey, value: Boolean?) {
        dataStore.edit { prefs ->
            if (value == null) prefs.remove(key.prefKey)
            else prefs[key.prefKey] = value
        }
    }

    override fun isUserCreated(): Boolean = getToken() != null
    override fun getUserFlow(): Flow<User?> {
        val idFlow = getStringFlow(StringKey.USER_ID)
        val firstNameFlow = getStringFlow(StringKey.USER_FIRST_NAME)
        val lastNameFlow = getStringFlow(StringKey.USER_LAST_NAME)
        val isStaffFlow = getBooleanFlow(BooleanKey.USER_IS_STAFF)
        val isAdminFlow = getBooleanFlow(BooleanKey.USER_IS_ADMIN)
        return combine(
            idFlow, firstNameFlow, lastNameFlow, isStaffFlow, isAdminFlow
        ) { id, firstName, lastName, isStaff, isAdmin ->
            if (id == null || firstName == null || lastName == null || isStaff == null || isAdmin == null) {
                null
            } else {
                User(
                    id = id,
                    firstName = firstName,
                    lastName = lastName,
                    isStaff = isStaff,
                    isAdmin = isAdmin
                )
            }
        }
    }
    override suspend fun saveUser(user: User) {
        saveString(StringKey.USER_ID, user.id)
        saveString(StringKey.USER_FIRST_NAME, user.firstName)
        saveString(StringKey.USER_LAST_NAME, user.lastName)
        saveBoolean(BooleanKey.USER_IS_STAFF, user.isStaff)
        saveBoolean(BooleanKey.USER_IS_ADMIN, user.isAdmin)
    }

    override fun getUserIsStaff() = getBoolean(BooleanKey.USER_IS_STAFF) ?: false

    override fun getToken(): String? = getString(StringKey.TOKEN)
    override suspend fun saveToken(token: String) = saveString(StringKey.TOKEN, token)

    override fun getTtsEnabled(): Boolean? = getBoolean(BooleanKey.TTS_ENABLED)
    override fun getTtsEnabledFlow(): Flow<Boolean?> = getBooleanFlow(BooleanKey.TTS_ENABLED)
    override suspend fun setTtsEnabled(enabled: Boolean) = saveBoolean(BooleanKey.TTS_ENABLED, enabled)
}