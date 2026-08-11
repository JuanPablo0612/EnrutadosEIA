package com.juanpablo0612.carpool.presentation.trip

import com.juanpablo0612.carpool.domain.trip.model.TripError
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_no_vehicle_selected
import enrutadoseia.composeapp.generated.resources.error_trip_departure_in_past
import enrutadoseia.composeapp.generated.resources.error_trip_not_found
import enrutadoseia.composeapp.generated.resources.error_unknown
import enrutadoseia.composeapp.generated.resources.error_user_not_authenticated
import org.jetbrains.compose.resources.StringResource

fun TripError.asStringResource(): StringResource = when (this) {
    TripError.TripNotFound -> Res.string.error_trip_not_found
    TripError.NoVehicleSelected -> Res.string.error_no_vehicle_selected
    TripError.UserNotAuthenticated -> Res.string.error_user_not_authenticated
    TripError.DepartureInPast -> Res.string.error_trip_departure_in_past
    TripError.Unknown -> Res.string.error_unknown
}
