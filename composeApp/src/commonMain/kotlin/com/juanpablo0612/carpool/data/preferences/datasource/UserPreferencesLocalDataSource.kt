package com.juanpablo0612.carpool.data.preferences.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

class UserPreferencesLocalDataSource(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val ONBOARDING_SEEN_KEY = booleanPreferencesKey("has_seen_onboarding")
    }

    suspend fun setOnboardingSeen() {
        dataStore.edit { it[ONBOARDING_SEEN_KEY] = true }
    }

    suspend fun hasSeenOnboarding(): Boolean = dataStore.data.first()[ONBOARDING_SEEN_KEY] ?: false
}
