package com.juanpablo0612.carpool.data.preferences.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreferencesLocalDataSource(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val ONBOARDING_SEEN_KEY = booleanPreferencesKey("has_seen_onboarding")

        // Keyed by user id: a device can be signed in to several accounts over time.
        private val VEHICLE_SUGGESTION_DISMISSED_KEY = stringSetPreferencesKey("vehicle_suggestion_dismissed_by")
    }

    suspend fun setOnboardingSeen() {
        dataStore.edit { it[ONBOARDING_SEEN_KEY] = true }
    }

    suspend fun hasSeenOnboarding(): Boolean = dataStore.data.first()[ONBOARDING_SEEN_KEY] ?: false

    fun isVehicleSuggestionDismissed(userId: String): Flow<Boolean> = dataStore.data
        .map { userId in it[VEHICLE_SUGGESTION_DISMISSED_KEY].orEmpty() }
        .distinctUntilChanged()

    suspend fun dismissVehicleSuggestion(userId: String) {
        dataStore.edit { it[VEHICLE_SUGGESTION_DISMISSED_KEY] = it[VEHICLE_SUGGESTION_DISMISSED_KEY].orEmpty() + userId }
    }
}
