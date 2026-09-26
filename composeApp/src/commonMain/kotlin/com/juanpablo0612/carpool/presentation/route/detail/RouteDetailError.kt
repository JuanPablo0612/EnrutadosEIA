package com.juanpablo0612.carpool.presentation.route.detail

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_route_delete_failed
import enrutadoseia.composeapp.generated.resources.error_route_duplicate_failed
import enrutadoseia.composeapp.generated.resources.error_route_not_found
import enrutadoseia.composeapp.generated.resources.error_route_save_failed
import org.jetbrains.compose.resources.StringResource

/**
 * Failures surfaced by the route detail screen, typed so their copy stays in `strings.xml`.
 */
sealed class RouteDetailError {
    data object NotFound : RouteDetailError()
    data object SaveFailed : RouteDetailError()
    data object DeleteFailed : RouteDetailError()
    data object DuplicateFailed : RouteDetailError()

    fun asStringResource(): StringResource = when (this) {
        NotFound -> Res.string.error_route_not_found
        SaveFailed -> Res.string.error_route_save_failed
        DeleteFailed -> Res.string.error_route_delete_failed
        DuplicateFailed -> Res.string.error_route_duplicate_failed
    }
}
