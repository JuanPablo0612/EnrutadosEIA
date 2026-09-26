package com.juanpablo0612.carpool.data.notification.model

import com.juanpablo0612.carpool.domain.notification.model.AppNotification
import com.juanpablo0612.carpool.domain.notification.model.NotificationType
import kotlinx.serialization.Serializable

/**
 * An in-app notification, written only by Cloud Functions as a [type] plus string [params] that
 * the app renders into localized text.
 */
@Serializable
data class NotificationDto(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val params: Map<String, String> = emptyMap(),
    val isRead: Boolean = false,
    val timestamp: Long = 0L,
) {
    fun toDomain(): AppNotification = AppNotification(
        id = id,
        userId = userId,
        type = NotificationType.fromKey(type),
        params = params,
        isRead = isRead,
        timestamp = timestamp,
    )
}
