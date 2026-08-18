package com.juanpablo0612.carpool.data.safety.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.safety.datasource.SafetyRemoteDataSource
import com.juanpablo0612.carpool.data.safety.model.EmergencyContactDto
import com.juanpablo0612.carpool.domain.safety.model.EmergencyContact
import com.juanpablo0612.carpool.domain.safety.model.SafetySettings
import com.juanpablo0612.carpool.domain.safety.repository.SafetyRepository

class SafetyRepositoryImpl(
    private val remoteDataSource: SafetyRemoteDataSource
) : SafetyRepository {

    override suspend fun getEmergencyContacts(userId: String): Result<List<EmergencyContact>> {
        return try {
            val contacts = remoteDataSource.getEmergencyContacts(userId).map { it.toDomain() }
            Result.success(contacts)
        } catch (_: Exception) {
            Result.failure(AppException.SafetyException.Unknown)
        }
    }

    override suspend fun addEmergencyContact(userId: String, contact: EmergencyContact): Result<Unit> {
        return try {
            val dto = EmergencyContactDto.fromDomain(contact)
            remoteDataSource.addEmergencyContact(userId, dto)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.SafetyException.Unknown)
        }
    }

    override suspend fun removeEmergencyContact(userId: String, contactId: String): Result<Unit> {
        return try {
            remoteDataSource.removeEmergencyContact(userId, contactId)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.SafetyException.Unknown)
        }
    }

    override suspend fun getSafetySettings(userId: String): Result<SafetySettings> {
        return try {
            Result.success(remoteDataSource.getSafetySettings(userId))
        } catch (_: Exception) {
            Result.failure(AppException.SafetyException.Unknown)
        }
    }

    override suspend fun updateSafetySettings(userId: String, settings: SafetySettings): Result<Unit> {
        return try {
            remoteDataSource.updateSafetySettings(userId, settings)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.SafetyException.Unknown)
        }
    }
}
