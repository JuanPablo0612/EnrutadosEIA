package com.juanpablo0612.carpool.presentation.notification

import com.juanpablo0612.carpool.core.exception.AppException
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

fun Throwable.toNotificationError(): NotificationError = when (this) {
    is AppException.NotificationException.Unknown -> NotificationError.Unknown
    else -> NotificationError.Unknown
}

fun NotificationError.asStringResource(): StringResource = when (this) {
    NotificationError.Unknown -> Res.string.error_unknown
}
