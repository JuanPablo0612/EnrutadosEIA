package com.juanpablo0612.carpool.presentation.notification

sealed class NotificationsEvent {
    data object NavigateBack : NotificationsEvent()
    data class NavigateTo(val deepLink: String) : NotificationsEvent()
}
