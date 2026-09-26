package com.juanpablo0612.carpool.domain.notification.model

data class AppNotification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    /** Structured values rendered into localized text; keys in [NotificationParams]. */
    val params: Map<String, String>,
    val isRead: Boolean,
    val timestamp: Long,
    /** Pre-rendered text and link of notifications written before params existed. */
    val legacyTitle: String? = null,
    val legacyBody: String? = null,
    val legacyDeepLink: String? = null,
)
