package com.juanpablo0612.carpool.presentation.route.list

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

sealed class RoutesListError {
    data object LoadFailed : RoutesListError()

    fun asStringResource(): StringResource = when (this) {
        LoadFailed -> Res.string.error_unknown
    }
}
