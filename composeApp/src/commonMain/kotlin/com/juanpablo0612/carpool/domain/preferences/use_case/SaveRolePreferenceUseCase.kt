package com.juanpablo0612.carpool.domain.preferences.use_case

import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.domain.preferences.UserPreferencesRepository

class SaveRolePreferenceUseCase(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(role: UserRole) = repository.saveRolePreference(role)
}
