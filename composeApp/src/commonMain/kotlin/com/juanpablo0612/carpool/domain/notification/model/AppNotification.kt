package com.juanpablo0612.carpool.domain.notification.model

data class AppNotification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    /** Structured values rendered into localized text; keys in [NotificationParams]. */
    val params: Map<String, String>,
    val isRead: Boolean,
    val timestamp: Long,
)
