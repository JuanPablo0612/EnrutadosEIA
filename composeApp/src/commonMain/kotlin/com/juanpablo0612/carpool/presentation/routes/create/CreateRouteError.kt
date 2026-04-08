package com.juanpablo0612.carpool.presentation.routes.create

import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

enum class CreateRouteError {
    OriginDestinationRequired,
    AtLeastOneWaypoint,
    AtLeastOneDay,
    UserNotAuthenticated,
    Unknown;

    fun asStringResource(): StringResource {
        return when (this) {
            OriginDestinationRequired -> Res.string.error_origin_destination_required
            AtLeastOneWaypoint -> Res.string.error_at_least_one_waypoint
            AtLeastOneDay -> Res.string.error_at_least_one_day
            UserNotAuthenticated -> Res.string.error_user_not_authenticated
            Unknown -> Res.string.error_unknown
        }
    }
}
