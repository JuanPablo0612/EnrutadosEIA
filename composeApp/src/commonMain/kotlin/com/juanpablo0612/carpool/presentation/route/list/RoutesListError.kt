package com.juanpablo0612.carpool.presentation.route.list

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_route_duplicate_failed
import enrutadoseia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

sealed class RoutesListError {
    data object LoadFailed : RoutesListError()
    data object DuplicateFailed : RoutesListError()

    fun asStringResource(): StringResource = when (this) {
        LoadFailed -> Res.string.error_unknown
        DuplicateFailed -> Res.string.error_route_duplicate_failed
    }
}
