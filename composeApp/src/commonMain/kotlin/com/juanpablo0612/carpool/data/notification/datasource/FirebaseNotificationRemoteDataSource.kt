package com.juanpablo0612.carpool.data.notification.datasource

import com.juanpablo0612.carpool.data.notification.model.NotificationDto
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseNotificationRemoteDataSource(
    private val firestore: FirebaseFirestore
) : NotificationRemoteDataSource {

    override fun getNotifications(userId: String): Flow<List<NotificationDto>> {
        return firestore.collection(COLLECTION)
            .document(userId)
            .collection(ITEMS_COLLECTION)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(NotificationDto.serializer()) }
            }
    }

    override suspend fun createNotification(notification: NotificationDto) {
        firestore.collection(COLLECTION)
            .document(notification.userId)
            .collection(ITEMS_COLLECTION)
            .document(notification.id)
            .set(NotificationDto.serializer(), notification)
    }

    override suspend fun markRead(userId: String, notificationId: String) {
        firestore.collection(COLLECTION)
            .document(userId)
            .collection(ITEMS_COLLECTION)
            .document(notificationId)
            .update("isRead" to true)
    }

    override suspend fun delete(userId: String, notificationId: String) {
        firestore.collection(COLLECTION)
            .document(userId)
            .collection(ITEMS_COLLECTION)
            .document(notificationId)
            .delete()
    }

    override suspend fun clearAll(userId: String) {
        val snapshot = firestore.collection(COLLECTION)
            .document(userId)
            .collection(ITEMS_COLLECTION)
            .get()
        snapshot.documents.forEach { it.reference.delete() }
    }

    companion object {
        private const val COLLECTION = "notifications"
        private const val ITEMS_COLLECTION = "items"
    }
}
