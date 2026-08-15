package com.juanpablo0612.carpool.presentation.routes.detail

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_route_delete_failed
import enrutadoseia.composeapp.generated.resources.error_route_not_found
import enrutadoseia.composeapp.generated.resources.error_route_save_failed
import org.jetbrains.compose.resources.StringResource

/**
 * Failures surfaced by the route detail screen.
 *
 * This replaces a plain `String?` that the ViewModel filled with Spanish literals — the last
 * untyped error in the presentation layer, and one that could not be localized because the copy
 * never reached `strings.xml`.
 */
sealed class RouteDetailError {
    data object NotFound : RouteDetailError()
    data object SaveFailed : RouteDetailError()
    data object DeleteFailed : RouteDetailError()

    fun asStringResource(): StringResource = when (this) {
        NotFound -> Res.string.error_route_not_found
        SaveFailed -> Res.string.error_route_save_failed
        DeleteFailed -> Res.string.error_route_delete_failed
    }
}
