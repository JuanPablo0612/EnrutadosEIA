package com.juanpablo0612.carpool.presentation.notification

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.notification_clear_all_failed
import enrutadoseia.composeapp.generated.resources.notification_delete_failed
import org.jetbrains.compose.resources.StringResource

sealed class NotificationActionError {
    data object DeleteFailed : NotificationActionError()
    data object ClearAllFailed : NotificationActionError()

    fun asStringResource(): StringResource = when (this) {
        DeleteFailed -> Res.string.notification_delete_failed
        ClearAllFailed -> Res.string.notification_clear_all_failed
    }
}
