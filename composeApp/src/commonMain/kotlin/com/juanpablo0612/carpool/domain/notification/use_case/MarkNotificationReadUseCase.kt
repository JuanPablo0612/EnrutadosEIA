package com.juanpablo0612.carpool.domain.notification.use_case

import com.juanpablo0612.carpool.domain.notification.repository.NotificationRepository

class MarkNotificationReadUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(notificationId: String): Result<Unit> =
        repository.markRead(notificationId)
}
