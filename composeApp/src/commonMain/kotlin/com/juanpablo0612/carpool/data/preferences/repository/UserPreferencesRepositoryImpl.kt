package com.juanpablo0612.carpool.data.preferences.repository

import com.juanpablo0612.carpool.data.preferences.datasource.UserPreferencesLocalDataSource
import com.juanpablo0612.carpool.domain.preferences.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class UserPreferencesRepositoryImpl(
    private val dataSource: UserPreferencesLocalDataSource
) : UserPreferencesRepository {

    override suspend fun setOnboardingSeen() {
        dataSource.setOnboardingSeen()
    }

    override suspend fun hasSeenOnboarding(): Boolean = dataSource.hasSeenOnboarding()

    // An unreadable preference file shows the suggestion again rather than failing Inicio.
    override fun isVehicleSuggestionDismissed(userId: String): Flow<Boolean> =
        dataSource.isVehicleSuggestionDismissed(userId).catch { emit(false) }

    override suspend fun dismissVehicleSuggestion(userId: String) {
        dataSource.dismissVehicleSuggestion(userId)
    }
}
