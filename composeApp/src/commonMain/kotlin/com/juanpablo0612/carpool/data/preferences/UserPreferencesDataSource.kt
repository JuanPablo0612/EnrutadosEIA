package com.juanpablo0612.carpool.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class UserPreferencesDataSource(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val ROLE_KEY = stringPreferencesKey("role_preference")
        private val ONBOARDING_SEEN_KEY = booleanPreferencesKey("has_seen_onboarding")
    }

    suspend fun saveRole(role: String) {
        dataStore.edit { it[ROLE_KEY] = role }
    }

    suspend fun getRole(): String? = dataStore.data.first()[ROLE_KEY]

    suspend fun clearRole() {
        dataStore.edit { it.remove(ROLE_KEY) }
    }

    suspend fun setOnboardingSeen() {
        dataStore.edit { it[ONBOARDING_SEEN_KEY] = true }
    }

    suspend fun hasSeenOnboarding(): Boolean = dataStore.data.first()[ONBOARDING_SEEN_KEY] ?: false
}
