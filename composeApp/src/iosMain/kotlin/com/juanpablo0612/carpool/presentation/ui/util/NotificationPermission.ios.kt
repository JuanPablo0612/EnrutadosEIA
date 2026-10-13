package com.juanpablo0612.carpool.presentation.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenNotificationSettingsURLString
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatus
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusEphemeral
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

private val grantedStatuses = setOf(
    UNAuthorizationStatusAuthorized,
    UNAuthorizationStatusProvisional,
    UNAuthorizationStatusEphemeral,
)

@Composable
actual fun rememberNotificationPermissionState(): NotificationPermissionState {
    val center = remember { UNUserNotificationCenter.currentNotificationCenter() }
    val scope = rememberCoroutineScope()
    // Unknown until the first read; counted as granted meanwhile so the banner doesn't flash.
    var status by remember { mutableStateOf<UNAuthorizationStatus?>(null) }

    // The user may change it in Settings while the app is in the background.
    LifecycleResumeEffect(center) {
        val read = scope.launch { status = center.authorizationStatus() }
        onPauseOrDispose { read.cancel() }
    }

    return remember(status, center, scope) {
        object : NotificationPermissionState {
            override val isGranted = status?.let { it in grantedStatuses } ?: true

            override fun request() {
                if (status != null && status != UNAuthorizationStatusNotDetermined) return
                // Called directly, not from a coroutine: the caller may navigate away right after,
                // and the system only shows the prompt once; later calls just report the answer.
                center.requestAuthorizationWithOptions(
                    UNAuthorizationOptionAlert or UNAuthorizationOptionSound,
                ) { _, _ ->
                    scope.launch { status = center.authorizationStatus() }
                }
            }

            override fun openSettings() {
                // Until the app has asked once, Settings has no notification switch for it.
                if (status == null || status == UNAuthorizationStatusNotDetermined) {
                    request()
                    return
                }
                val url = NSURL.URLWithString(UIApplicationOpenNotificationSettingsURLString) ?: return
                UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
            }
        }
    }
}

private suspend fun UNUserNotificationCenter.authorizationStatus(): UNAuthorizationStatus =
    suspendCancellableCoroutine { continuation ->
        getNotificationSettingsWithCompletionHandler { settings ->
            continuation.resume(settings?.authorizationStatus ?: UNAuthorizationStatusNotDetermined)
        }
    }
