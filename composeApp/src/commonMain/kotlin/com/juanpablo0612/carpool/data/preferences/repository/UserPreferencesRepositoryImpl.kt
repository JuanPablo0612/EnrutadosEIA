package com.juanpablo0612.carpool.data.preferences.repository

import com.juanpablo0612.carpool.data.preferences.datasource.UserPreferencesLocalDataSource
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.domain.preferences.repository.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val dataSource: UserPreferencesLocalDataSource
) : UserPreferencesRepository {

    override suspend fun saveRolePreference(role: UserRole) {
        dataSource.saveRole(role.toKey())
    }

    override suspend fun getRolePreference(): UserRole? {
        return dataSource.getRole()?.toUserRole()
    }

    override suspend fun clearRolePreference() {
        dataSource.clearRole()
    }

    override suspend fun setOnboardingSeen() {
        dataSource.setOnboardingSeen()
    }

    override suspend fun hasSeenOnboarding(): Boolean = dataSource.hasSeenOnboarding()

    private fun UserRole.toKey(): String = when (this) {
        UserRole.Driver -> "driver"
        UserRole.Passenger -> "passenger"
    }

    private fun String.toUserRole(): UserRole? = when (this) {
        "driver" -> UserRole.Driver
        "passenger" -> UserRole.Passenger
        else -> null
    }
}
