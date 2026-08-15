package com.juanpablo0612.carpool.presentation.notifications

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.notification.model.NotificationsError
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

fun Throwable.toNotificationsError(): NotificationsError = when (this) {
    is AppException.NotificationException.Unknown -> NotificationsError.Unknown
    else -> NotificationsError.Unknown
}

fun NotificationsError.asStringResource(): StringResource = when (this) {
    NotificationsError.Unknown -> Res.string.error_unknown
}
