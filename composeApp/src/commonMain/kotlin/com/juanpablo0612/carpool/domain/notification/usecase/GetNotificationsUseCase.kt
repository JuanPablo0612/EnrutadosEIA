package com.juanpablo0612.carpool.domain.notification.usecase

import com.juanpablo0612.carpool.domain.notification.model.AppNotification
import com.juanpablo0612.carpool.domain.notification.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

class GetNotificationsUseCase(private val repository: NotificationRepository) {
    operator fun invoke(userId: String): Flow<List<AppNotification>> =
        repository.getNotifications(userId)
}
