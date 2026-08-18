package com.juanpablo0612.carpool.domain.notification.model

sealed class NotificationError {
    data object Unknown : NotificationError()
}
