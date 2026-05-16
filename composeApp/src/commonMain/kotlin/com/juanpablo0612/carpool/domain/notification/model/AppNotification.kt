package com.juanpablo0612.carpool.domain.notification.model

data class AppNotification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val deepLink: String?,
    val isRead: Boolean,
    val timestamp: Long
)
