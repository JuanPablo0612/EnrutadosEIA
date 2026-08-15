package com.juanpablo0612.carpool.domain.notification.model

sealed class NotificationsError {
    data object Unknown : NotificationsError()
}
