package com.juanpablo0612.carpool.data.notification.datasource

import com.juanpablo0612.carpool.data.notification.model.NotificationDto
import kotlinx.coroutines.flow.Flow

interface NotificationRemoteDataSource {
    fun getNotifications(userId: String): Flow<List<NotificationDto>>
    suspend fun createNotification(notification: NotificationDto)
    suspend fun markRead(userId: String, notificationId: String)
    suspend fun delete(userId: String, notificationId: String)
    suspend fun clearAll(userId: String)
}
