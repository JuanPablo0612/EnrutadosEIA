package com.juanpablo0612.carpool.presentation.notifications

import com.juanpablo0612.carpool.domain.notification.model.AppNotification

data class NotificationsUiState(
    val notifications: List<AppNotification> = emptyList(),
    val isLoading: Boolean = true
) {
    val unreadCount: Int get() = notifications.count { !it.isRead }
}
