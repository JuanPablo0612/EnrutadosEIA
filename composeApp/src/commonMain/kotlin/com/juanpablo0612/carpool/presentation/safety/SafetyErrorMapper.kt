package com.juanpablo0612.carpool.presentation.safety

import com.juanpablo0612.carpool.core.exception.AppException
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_unknown
import enrutadoseia.composeapp.generated.resources.safety_max_contacts_reached
import org.jetbrains.compose.resources.StringResource

fun Throwable.toSafetyError(): SafetyError = when (this) {
    is AppException.SafetyException.MaxContactsReached -> SafetyError.MaxContactsReached
    else -> SafetyError.Unknown
}

fun SafetyError.asStringResource(): StringResource = when (this) {
    SafetyError.MaxContactsReached -> Res.string.safety_max_contacts_reached
    SafetyError.Unknown -> Res.string.error_unknown
}
