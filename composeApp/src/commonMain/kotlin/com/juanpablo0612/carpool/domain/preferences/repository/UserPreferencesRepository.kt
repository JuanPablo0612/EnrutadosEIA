package com.juanpablo0612.carpool.domain.preferences.repository


interface UserPreferencesRepository {
    suspend fun setOnboardingSeen()
    suspend fun hasSeenOnboarding(): Boolean
}
