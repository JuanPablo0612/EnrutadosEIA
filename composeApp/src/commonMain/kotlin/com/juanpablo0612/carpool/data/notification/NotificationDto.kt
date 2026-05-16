package com.juanpablo0612.carpool.data.notification

import com.juanpablo0612.carpool.domain.notification.model.AppNotification
import com.juanpablo0612.carpool.domain.notification.model.NotificationType
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val title: String = "",
    val body: String = "",
    val deepLink: String? = null,
    val isRead: Boolean = false,
    val timestamp: Long = 0L
) {
    fun toDomain(): AppNotification = AppNotification(
        id = id,
        userId = userId,
        type = NotificationType.fromKey(type),
        title = title,
        body = body,
        deepLink = deepLink,
        isRead = isRead,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(notification: AppNotification): NotificationDto = NotificationDto(
            id = notification.id,
            userId = notification.userId,
            type = notification.type.key,
            title = notification.title,
            body = notification.body,
            deepLink = notification.deepLink,
            isRead = notification.isRead,
            timestamp = notification.timestamp
        )
    }
}
