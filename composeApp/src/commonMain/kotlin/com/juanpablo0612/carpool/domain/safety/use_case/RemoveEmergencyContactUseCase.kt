package com.juanpablo0612.carpool.domain.safety.use_case

import com.juanpablo0612.carpool.domain.safety.repository.SafetyRepository

class RemoveEmergencyContactUseCase(private val repository: SafetyRepository) {
    suspend operator fun invoke(userId: String, contactId: String): Result<Unit> =
        repository.removeEmergencyContact(userId, contactId)
}
