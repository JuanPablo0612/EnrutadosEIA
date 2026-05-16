package com.juanpablo0612.carpool.domain.notification.use_case

import com.juanpablo0612.carpool.domain.notification.repository.NotificationRepository

class ClearAllNotificationsUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(userId: String): Result<Unit> =
        repository.clearAll(userId)
}
