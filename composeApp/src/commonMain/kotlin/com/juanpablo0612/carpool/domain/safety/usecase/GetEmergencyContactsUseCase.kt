package com.juanpablo0612.carpool.domain.safety.usecase

import com.juanpablo0612.carpool.domain.safety.model.EmergencyContact
import com.juanpablo0612.carpool.domain.safety.repository.SafetyRepository

class GetEmergencyContactsUseCase(private val repository: SafetyRepository) {
    suspend operator fun invoke(userId: String): Result<List<EmergencyContact>> =
        repository.getEmergencyContacts(userId)
}
