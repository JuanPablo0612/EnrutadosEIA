package com.juanpablo0612.carpool.presentation.routes.detail

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_at_least_one_day
import enrutadoseia.composeapp.generated.resources.error_at_least_one_waypoint
import enrutadoseia.composeapp.generated.resources.error_origin_destination_required
import enrutadoseia.composeapp.generated.resources.error_route_not_found
import enrutadoseia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

sealed class RouteDetailError {
    data object RouteNotFound : RouteDetailError()
    data object OriginDestinationRequired : RouteDetailError()
    data object Unknown : RouteDetailError()

    fun asStringResource(): StringResource = when (this) {
        RouteNotFound -> Res.string.error_route_not_found
        OriginDestinationRequired -> Res.string.error_origin_destination_required
        Unknown -> Res.string.error_unknown
    }
}
