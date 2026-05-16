package com.juanpablo0612.carpool.domain.safety.repository

import com.juanpablo0612.carpool.domain.safety.model.EmergencyContact
import com.juanpablo0612.carpool.domain.safety.model.SafetySettings

interface SafetyRepository {
    suspend fun getEmergencyContacts(userId: String): Result<List<EmergencyContact>>
    suspend fun addEmergencyContact(userId: String, contact: EmergencyContact): Result<Unit>
    suspend fun removeEmergencyContact(userId: String, contactId: String): Result<Unit>
    suspend fun getSafetySettings(userId: String): Result<SafetySettings>
    suspend fun updateSafetySettings(userId: String, settings: SafetySettings): Result<Unit>
}
