package com.juanpablo0612.carpool.domain.safety.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.safety.model.EmergencyContact
import com.juanpablo0612.carpool.domain.safety.repository.SafetyRepository
import kotlin.time.Clock

class AddEmergencyContactUseCase(private val repository: SafetyRepository) {
    suspend operator fun invoke(
        userId: String,
        name: String,
        phone: String,
        currentCount: Int
    ): Result<Unit> {
        if (currentCount >= 2) return Result.failure(AppException.SafetyException.MaxContactsReached)
        val contact = EmergencyContact(
            id = "${Clock.System.now().toEpochMilliseconds()}",
            name = name.trim(),
            phone = phone.trim()
        )
        return repository.addEmergencyContact(userId, contact)
    }
}
