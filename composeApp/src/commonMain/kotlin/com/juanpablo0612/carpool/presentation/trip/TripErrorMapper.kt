package com.juanpablo0612.carpool.presentation.trip

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_no_vehicle_selected
import enrutadoseia.composeapp.generated.resources.error_origin_destination_required
import enrutadoseia.composeapp.generated.resources.error_publish_week_nothing_selected
import enrutadoseia.composeapp.generated.resources.error_route_name_required
import enrutadoseia.composeapp.generated.resources.error_route_saved_trip_failed
import enrutadoseia.composeapp.generated.resources.error_trip_contribution_out_of_range
import enrutadoseia.composeapp.generated.resources.error_trip_departure_in_past
import enrutadoseia.composeapp.generated.resources.error_trip_departure_too_far
import enrutadoseia.composeapp.generated.resources.error_trip_departure_too_soon
import enrutadoseia.composeapp.generated.resources.error_trip_message_too_long
import enrutadoseia.composeapp.generated.resources.error_trip_not_found
import enrutadoseia.composeapp.generated.resources.error_trip_same_origin_destination
import enrutadoseia.composeapp.generated.resources.error_trip_seats_out_of_range
import enrutadoseia.composeapp.generated.resources.error_unknown
import enrutadoseia.composeapp.generated.resources.error_user_not_authenticated
import org.jetbrains.compose.resources.StringResource

fun TripError.asStringResource(): StringResource = when (this) {
    TripError.TripNotFound -> Res.string.error_trip_not_found
    TripError.NoVehicleSelected -> Res.string.error_no_vehicle_selected
    TripError.UserNotAuthenticated -> Res.string.error_user_not_authenticated
    TripError.DepartureInPast -> Res.string.error_trip_departure_in_past
    TripError.OriginDestinationRequired -> Res.string.error_origin_destination_required
    TripError.SameOriginDestination -> Res.string.error_trip_same_origin_destination
    TripError.DepartureTooSoon -> Res.string.error_trip_departure_too_soon
    TripError.DepartureTooFar -> Res.string.error_trip_departure_too_far
    TripError.SeatsOutOfRange -> Res.string.error_trip_seats_out_of_range
    TripError.ContributionOutOfRange -> Res.string.error_trip_contribution_out_of_range
    TripError.MessageTooLong -> Res.string.error_trip_message_too_long
    TripError.RouteNameRequired -> Res.string.error_route_name_required
    TripError.RouteSavedButTripFailed -> Res.string.error_route_saved_trip_failed
    TripError.NothingSelected -> Res.string.error_publish_week_nothing_selected
    TripError.Unknown -> Res.string.error_unknown
}

fun TripValidationError.toTripError(): TripError = when (this) {
    TripValidationError.OriginDestinationRequired -> TripError.OriginDestinationRequired
    TripValidationError.SameOriginDestination -> TripError.SameOriginDestination
    TripValidationError.DepartureTooSoon -> TripError.DepartureTooSoon
    TripValidationError.DepartureTooFar -> TripError.DepartureTooFar
    TripValidationError.NoVehicleSelected -> TripError.NoVehicleSelected
    TripValidationError.SeatsOutOfRange -> TripError.SeatsOutOfRange
    TripValidationError.ContributionOutOfRange -> TripError.ContributionOutOfRange
    TripValidationError.MessageTooLong -> TripError.MessageTooLong
    TripValidationError.RouteNameRequired -> TripError.RouteNameRequired
}

fun Throwable.toTripError(): TripError = when (this) {
    is AppException.TripException.Invalid -> errors.firstOrNull()?.toTripError() ?: TripError.Unknown
    is AppException.TripException.NotAuthenticated -> TripError.UserNotAuthenticated
    is AppException.TripException.VehicleNotFound -> TripError.NoVehicleSelected
    is AppException.TripException.RouteSavedTripFailed -> TripError.RouteSavedButTripFailed
    else -> TripError.Unknown
}
