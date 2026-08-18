package com.juanpablo0612.carpool.domain.preferences.usecase

import com.juanpablo0612.carpool.domain.preferences.UserPreferencesRepository

class GetOnboardingSeenUseCase(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(): Boolean = repository.hasSeenOnboarding()
}
