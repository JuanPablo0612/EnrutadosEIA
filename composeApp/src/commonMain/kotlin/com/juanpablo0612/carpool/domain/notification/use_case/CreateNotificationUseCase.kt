package com.juanpablo0612.carpool.domain.notification.use_case

import com.juanpablo0612.carpool.domain.notification.model.AppNotification
import com.juanpablo0612.carpool.domain.notification.model.NotificationType
import com.juanpablo0612.carpool.domain.notification.repository.NotificationRepository
import kotlin.time.Clock

class CreateNotificationUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(
        userId: String,
        type: NotificationType,
        title: String,
        body: String,
        deepLink: String? = null
    ): Result<Unit> {
        val notification = AppNotification(
            id = "${Clock.System.now().toEpochMilliseconds()}_$userId",
            userId = userId,
            type = type,
            title = title,
            body = body,
            deepLink = deepLink,
            isRead = false,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
        return repository.createNotification(notification)
    }
}
