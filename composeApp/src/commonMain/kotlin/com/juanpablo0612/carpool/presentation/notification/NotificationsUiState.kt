package com.juanpablo0612.carpool.presentation.notification

import com.juanpablo0612.carpool.domain.notification.model.AppNotification
import com.juanpablo0612.carpool.domain.notification.model.NotificationError

data class NotificationsUiState(
    val notifications: List<AppNotification> = emptyList(),
    val isLoading: Boolean = true,
    val error: NotificationError? = null,
    val showClearAllDialog: Boolean = false,
    val actionError: Boolean = false
) {
    val unreadCount: Int get() = notifications.count { !it.isRead }
}
