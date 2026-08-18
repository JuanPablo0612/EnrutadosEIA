package com.juanpablo0612.carpool.data.safety.datasource

import com.juanpablo0612.carpool.data.safety.model.EmergencyContactDto
import com.juanpablo0612.carpool.domain.safety.model.SafetySettings
import dev.gitlive.firebase.firestore.FirebaseFirestore

class FirebaseSafetyRemoteDataSource(
    private val firestore: FirebaseFirestore
) : SafetyRemoteDataSource {

    override suspend fun getEmergencyContacts(userId: String): List<EmergencyContactDto> {
        val snapshot = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(CONTACTS_COLLECTION)
            .get()
        return snapshot.documents.map { it.data(EmergencyContactDto.serializer()) }
    }

    override suspend fun addEmergencyContact(userId: String, contact: EmergencyContactDto) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(CONTACTS_COLLECTION)
            .document(contact.id)
            .set(EmergencyContactDto.serializer(), contact)
    }

    override suspend fun removeEmergencyContact(userId: String, contactId: String) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(CONTACTS_COLLECTION)
            .document(contactId)
            .delete()
    }

    override suspend fun getSafetySettings(userId: String): SafetySettings {
        val doc = firestore.collection(USERS_COLLECTION).document(userId).get()
        val autoShare = runCatching { doc.get<Boolean>("autoShareTrip") }.getOrNull() ?: true
        val vibrateSos = runCatching { doc.get<Boolean>("vibrateSos") }.getOrNull() ?: true
        return SafetySettings(autoShareTrip = autoShare, vibrateSos = vibrateSos)
    }

    override suspend fun updateSafetySettings(userId: String, settings: SafetySettings) {
        firestore.collection(USERS_COLLECTION).document(userId)
            .update("autoShareTrip" to settings.autoShareTrip, "vibrateSos" to settings.vibrateSos)
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val CONTACTS_COLLECTION = "emergency_contacts"
    }
}
