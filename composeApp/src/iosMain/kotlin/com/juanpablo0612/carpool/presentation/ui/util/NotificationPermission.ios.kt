package com.juanpablo0612.carpool.presentation.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

// No push on the inactive iOS scaffold yet (see CLAUDE.md), so there is nothing to ask for.
@Composable
actual fun rememberNotificationPermissionState(): NotificationPermissionState = remember {
    object : NotificationPermissionState {
        override val isGranted = true
        override fun request() = Unit
        override fun openSettings() = Unit
    }
}
