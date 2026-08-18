package com.juanpablo0612.carpool.data.safety.datasource

import com.juanpablo0612.carpool.data.safety.model.EmergencyContactDto
import com.juanpablo0612.carpool.domain.safety.model.SafetySettings

interface SafetyRemoteDataSource {
    suspend fun getEmergencyContacts(userId: String): List<EmergencyContactDto>
    suspend fun addEmergencyContact(userId: String, contact: EmergencyContactDto)
    suspend fun removeEmergencyContact(userId: String, contactId: String)
    suspend fun getSafetySettings(userId: String): SafetySettings
    suspend fun updateSafetySettings(userId: String, settings: SafetySettings)
}
