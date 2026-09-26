package com.juanpablo0612.carpool.presentation.ui.util

import androidx.compose.runtime.Composable

/** Whether the app may show system notifications, and how to ask for it. */
interface NotificationPermissionState {
    val isGranted: Boolean

    /**
     * Shows the system permission dialog if the platform still allows asking; otherwise does
     * nothing. Safe to call right after a meaningful action (e.g. requesting a seat).
     */
    fun request()

    /** Opens the app's notification settings, for when the user explicitly asks to enable them. */
    fun openSettings()
}

@Composable
expect fun rememberNotificationPermissionState(): NotificationPermissionState
