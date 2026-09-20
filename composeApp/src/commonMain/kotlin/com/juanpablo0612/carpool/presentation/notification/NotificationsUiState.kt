package com.juanpablo0612.carpool.presentation.notification

import com.juanpablo0612.carpool.domain.notification.model.AppNotification

data class NotificationsUiState(
    val notifications: List<AppNotification> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: NotificationError? = null,
    val showClearAllDialog: Boolean = false,
    val pendingDeleteId: String? = null,
    val actionError: NotificationActionError? = null
) {
    val unreadCount: Int get() = notifications.count { !it.isRead }
}
