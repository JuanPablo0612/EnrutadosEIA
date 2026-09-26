package com.juanpablo0612.carpool.presentation.ui.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect

@Composable
actual fun rememberNotificationPermissionState(): NotificationPermissionState {
    val context = LocalContext.current
    var isGranted by remember { mutableStateOf(context.notificationsAllowed()) }
    // The system dialog can only be shown a limited number of times; after one answer, further
    // automatic prompts are skipped and only an explicit settings visit remains.
    var hasAsked by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        isGranted = context.notificationsAllowed()
    }

    // The user may change it in system settings while the app is in the background.
    LifecycleResumeEffect(Unit) {
        isGranted = context.notificationsAllowed()
        onPauseOrDispose {}
    }

    return remember(isGranted, hasAsked, launcher) {
        object : NotificationPermissionState {
            override val isGranted = isGranted

            override fun request() {
                if (isGranted || hasAsked || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
                hasAsked = true
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            override fun openSettings() {
                context.startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    }
}

private fun Context.notificationsAllowed(): Boolean {
    val permitted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED
    return permitted && NotificationManagerCompat.from(this).areNotificationsEnabled()
}
