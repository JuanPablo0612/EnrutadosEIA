package com.juanpablo0612.carpool.presentation.notification

import com.juanpablo0612.carpool.domain.notification.model.AppNotification

sealed class NotificationsAction {
    data class OnNotificationClick(val notification: AppNotification) : NotificationsAction()
    data class OnSwipeToDelete(val id: String) : NotificationsAction()
    data object OnConfirmDelete : NotificationsAction()
    data object OnDismissDeleteConfirm : NotificationsAction()
    data object OnClearAllClick : NotificationsAction()
    data object OnClearAllConfirmed : NotificationsAction()
    data object OnClearAllDismissed : NotificationsAction()
    data object OnRetry : NotificationsAction()
    data object Refresh : NotificationsAction()
    data object OnDismissActionError : NotificationsAction()
    data object OnBackClick : NotificationsAction()
}
