package com.juanpablo0612.carpool.data.preferences

import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.domain.preferences.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val dataSource: UserPreferencesDataSource
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
