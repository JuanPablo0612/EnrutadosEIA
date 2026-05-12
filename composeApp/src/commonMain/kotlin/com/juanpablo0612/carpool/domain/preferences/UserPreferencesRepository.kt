package com.juanpablo0612.carpool.domain.preferences

import com.juanpablo0612.carpool.domain.auth.model.UserRole

interface UserPreferencesRepository {
    suspend fun saveRolePreference(role: UserRole)
    suspend fun getRolePreference(): UserRole?
    suspend fun clearRolePreference()
}
