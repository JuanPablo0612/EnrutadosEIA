package com.juanpablo0612.carpool.data.safety

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.safety.model.EmergencyContact
import com.juanpablo0612.carpool.domain.safety.model.SafetySettings
import com.juanpablo0612.carpool.domain.safety.repository.SafetyRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore

class SafetyRepositoryImpl(private val firestore: FirebaseFirestore) : SafetyRepository {

    override suspend fun getEmergencyContacts(userId: String): Result<List<EmergencyContact>> {
        return try {
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CONTACTS_COLLECTION)
                .get()
            val contacts = snapshot.documents.map { it.data(EmergencyContactDto.serializer()).toDomain() }
            Result.success(contacts)
        } catch (e: Exception) {
            Result.failure(AppException.SafetyException.Unknown)
        }
    }

    override suspend fun addEmergencyContact(userId: String, contact: EmergencyContact): Result<Unit> {
        return try {
            val dto = EmergencyContactDto.fromDomain(contact)
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CONTACTS_COLLECTION)
                .document(contact.id)
                .set(EmergencyContactDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.SafetyException.Unknown)
        }
    }

    override suspend fun removeEmergencyContact(userId: String, contactId: String): Result<Unit> {
        return try {
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CONTACTS_COLLECTION)
                .document(contactId)
                .delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.SafetyException.Unknown)
        }
    }

    override suspend fun getSafetySettings(userId: String): Result<SafetySettings> {
        return try {
            val doc = firestore.collection(USERS_COLLECTION).document(userId).get()
            val autoShare = runCatching { doc.get<Boolean>("autoShareTrip") }.getOrNull() ?: true
            val vibrateSos = runCatching { doc.get<Boolean>("vibrateSos") }.getOrNull() ?: true
            Result.success(SafetySettings(autoShareTrip = autoShare, vibrateSos = vibrateSos))
        } catch (e: Exception) {
            Result.success(SafetySettings())
        }
    }

    override suspend fun updateSafetySettings(userId: String, settings: SafetySettings): Result<Unit> {
        return try {
            firestore.collection(USERS_COLLECTION).document(userId)
                .update("autoShareTrip" to settings.autoShareTrip, "vibrateSos" to settings.vibrateSos)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.SafetyException.Unknown)
        }
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val CONTACTS_COLLECTION = "emergency_contacts"
    }
}
