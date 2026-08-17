package com.juanpablo0612.carpool.presentation.notifications

import com.juanpablo0612.carpool.domain.notification.model.AppNotification

sealed class NotificationsAction {
    data class OnNotificationClick(val notification: AppNotification) : NotificationsAction()
    data class OnDismiss(val id: String) : NotificationsAction()
    data object OnClearAllClick : NotificationsAction()
    data object OnClearAllConfirmed : NotificationsAction()
    data object OnClearAllDismissed : NotificationsAction()
    data object OnRetry : NotificationsAction()
    data object OnDismissActionError : NotificationsAction()
    data object OnBackClick : NotificationsAction()
}
