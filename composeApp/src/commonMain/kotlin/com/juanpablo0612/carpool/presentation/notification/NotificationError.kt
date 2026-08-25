package com.juanpablo0612.carpool.presentation.notification

sealed class NotificationError {
    data object Unknown : NotificationError()
}
