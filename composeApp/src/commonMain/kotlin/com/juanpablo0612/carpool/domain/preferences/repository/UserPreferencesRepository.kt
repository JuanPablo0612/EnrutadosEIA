package com.juanpablo0612.carpool.domain.preferences.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    suspend fun setOnboardingSeen()
    suspend fun hasSeenOnboarding(): Boolean

    /** Whether [userId] hid the suggestion to register a vehicle on this device. */
    fun isVehicleSuggestionDismissed(userId: String): Flow<Boolean>
    suspend fun dismissVehicleSuggestion(userId: String)
}
