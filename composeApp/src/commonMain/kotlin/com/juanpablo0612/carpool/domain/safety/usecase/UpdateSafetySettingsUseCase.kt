package com.juanpablo0612.carpool.domain.safety.usecase

import com.juanpablo0612.carpool.domain.safety.model.SafetySettings
import com.juanpablo0612.carpool.domain.safety.repository.SafetyRepository

class UpdateSafetySettingsUseCase(private val repository: SafetyRepository) {
    suspend operator fun invoke(userId: String, settings: SafetySettings): Result<Unit> =
        repository.updateSafetySettings(userId, settings)
}
