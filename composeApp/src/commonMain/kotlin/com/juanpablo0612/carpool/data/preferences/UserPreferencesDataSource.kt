package com.juanpablo0612.carpool.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class UserPreferencesDataSource(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val ROLE_KEY = stringPreferencesKey("role_preference")
    }

    suspend fun saveRole(role: String) {
        dataStore.edit { it[ROLE_KEY] = role }
    }

    suspend fun getRole(): String? = dataStore.data.first()[ROLE_KEY]

    suspend fun clearRole() {
        dataStore.edit { it.remove(ROLE_KEY) }
    }
}
