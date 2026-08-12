package com.juanpablo0612.carpool.presentation.safety

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.safety_error_name_empty
import enrutadoseia.composeapp.generated.resources.safety_error_phone_empty
import org.jetbrains.compose.resources.StringResource

sealed class SafetyContactFieldError {
    data object NameEmpty : SafetyContactFieldError()
    data object PhoneEmpty : SafetyContactFieldError()

    fun asStringResource(): StringResource = when (this) {
        NameEmpty -> Res.string.safety_error_name_empty
        PhoneEmpty -> Res.string.safety_error_phone_empty
    }
}
