package com.juanpablo0612.carpool.domain.safety.usecase

import com.juanpablo0612.carpool.domain.safety.model.SafetySettings
import com.juanpablo0612.carpool.domain.safety.repository.SafetyRepository

class GetSafetySettingsUseCase(private val repository: SafetyRepository) {
    suspend operator fun invoke(userId: String): Result<SafetySettings> =
        repository.getSafetySettings(userId)
}
