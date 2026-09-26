package com.juanpablo0612.carpool.data.notification.model

import com.juanpablo0612.carpool.domain.notification.model.AppNotification
import com.juanpablo0612.carpool.domain.notification.model.NotificationType
import kotlinx.serialization.Serializable

/**
 * An in-app notification, written only by Cloud Functions. Current documents carry [type] plus
 * string [params]; [title], [body] and [deepLink] are only present on documents written before
 * that, by older app builds.
 */
@Serializable
data class NotificationDto(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val params: Map<String, String> = emptyMap(),
    val isRead: Boolean = false,
    val timestamp: Long = 0L,
    val title: String = "",
    val body: String = "",
    val deepLink: String? = null,
) {
    fun toDomain(): AppNotification = AppNotification(
        id = id,
        userId = userId,
        type = NotificationType.fromKey(type),
        params = params,
        isRead = isRead,
        timestamp = timestamp,
        legacyTitle = title.ifBlank { null },
        legacyBody = body.ifBlank { null },
        legacyDeepLink = deepLink?.ifBlank { null },
    )
}
