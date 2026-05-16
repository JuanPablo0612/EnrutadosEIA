package com.juanpablo0612.carpool.domain.notification.repository

import com.juanpablo0612.carpool.domain.notification.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(userId: String): Flow<List<AppNotification>>
    suspend fun createNotification(notification: AppNotification): Result<Unit>
    suspend fun markRead(notificationId: String): Result<Unit>
    suspend fun delete(notificationId: String): Result<Unit>
    suspend fun clearAll(userId: String): Result<Unit>
}
