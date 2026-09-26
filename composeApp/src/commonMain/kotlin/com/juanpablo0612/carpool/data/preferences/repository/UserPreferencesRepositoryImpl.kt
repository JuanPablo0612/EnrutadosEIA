package com.juanpablo0612.carpool.data.preferences.repository

import com.juanpablo0612.carpool.data.preferences.datasource.UserPreferencesLocalDataSource
import com.juanpablo0612.carpool.domain.preferences.repository.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val dataSource: UserPreferencesLocalDataSource
) : UserPreferencesRepository {

    override suspend fun setOnboardingSeen() {
        dataSource.setOnboardingSeen()
    }

    override suspend fun hasSeenOnboarding(): Boolean = dataSource.hasSeenOnboarding()
}
