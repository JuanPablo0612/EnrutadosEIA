package com.juanpablo0612.carpool.data.notification

import com.juanpablo0612.carpool.domain.notification.model.AppNotification
import com.juanpablo0612.carpool.domain.notification.repository.NotificationRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl(private val firestore: FirebaseFirestore) : NotificationRepository {

    override fun getNotifications(userId: String): Flow<List<AppNotification>> {
        return firestore.collection(COLLECTION)
            .document(userId)
            .collection(ITEMS_COLLECTION)
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.data(NotificationDto.serializer()).toDomain() }
                    .sortedByDescending { it.timestamp }
            }
    }

    override suspend fun createNotification(notification: AppNotification): Result<Unit> {
        return try {
            val dto = NotificationDto.fromDomain(notification)
            firestore.collection(COLLECTION)
                .document(notification.userId)
                .collection(ITEMS_COLLECTION)
                .document(notification.id)
                .set(NotificationDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markRead(notificationId: String): Result<Unit> {
        return try {
            val snapshot = firestore.collection(COLLECTION).get()
            snapshot.documents.forEach { userDoc ->
                val itemRef = firestore.collection(COLLECTION)
                    .document(userDoc.id)
                    .collection(ITEMS_COLLECTION)
                    .document(notificationId)
                val item = runCatching { itemRef.get() }.getOrNull()
                if (item?.exists == true) {
                    itemRef.update("isRead" to true)
                    return Result.success(Unit)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(notificationId: String): Result<Unit> {
        return try {
            val snapshot = firestore.collection(COLLECTION).get()
            snapshot.documents.forEach { userDoc ->
                val itemRef = firestore.collection(COLLECTION)
                    .document(userDoc.id)
                    .collection(ITEMS_COLLECTION)
                    .document(notificationId)
                val item = runCatching { itemRef.get() }.getOrNull()
                if (item?.exists == true) {
                    itemRef.delete()
                    return Result.success(Unit)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearAll(userId: String): Result<Unit> {
        return try {
            val snapshot = firestore.collection(COLLECTION)
                .document(userId)
                .collection(ITEMS_COLLECTION)
                .get()
            snapshot.documents.forEach { it.reference.delete() }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val COLLECTION = "notifications"
        private const val ITEMS_COLLECTION = "items"
    }
}
