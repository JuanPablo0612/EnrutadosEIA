package com.juanpablo0612.carpool.domain.preferences.use_case

import com.juanpablo0612.carpool.domain.preferences.UserPreferencesRepository

class ClearRolePreferenceUseCase(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke() = repository.clearRolePreference()
}
