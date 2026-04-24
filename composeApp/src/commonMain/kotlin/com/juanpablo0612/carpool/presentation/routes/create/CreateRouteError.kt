package com.juanpablo0612.carpool.presentation.routes.create

import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

sealed class CreateRouteError {
    data object OriginDestinationRequired : CreateRouteError()
    data object UserNotAuthenticated : CreateRouteError()
    data object Unknown : CreateRouteError()

    fun asStringResource(): StringResource = when (this) {
        OriginDestinationRequired -> Res.string.error_origin_destination_required
        UserNotAuthenticated -> Res.string.error_user_not_authenticated
        Unknown -> Res.string.error_unknown
    }
}
