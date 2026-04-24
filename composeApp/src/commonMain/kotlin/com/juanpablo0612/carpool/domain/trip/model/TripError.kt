package com.juanpablo0612.carpool.domain.trip.model

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_no_vehicle_selected
import enrutadoseia.composeapp.generated.resources.error_trip_not_found
import enrutadoseia.composeapp.generated.resources.error_unknown
import enrutadoseia.composeapp.generated.resources.error_user_not_authenticated
import org.jetbrains.compose.resources.StringResource

sealed class TripError {
    data object TripNotFound : TripError()
    data object NoVehicleSelected : TripError()
    data object UserNotAuthenticated : TripError()
    data object Unknown : TripError()

    fun asStringResource(): StringResource = when (this) {
        TripNotFound -> Res.string.error_trip_not_found
        NoVehicleSelected -> Res.string.error_no_vehicle_selected
        UserNotAuthenticated -> Res.string.error_user_not_authenticated
        Unknown -> Res.string.error_unknown
    }
}
