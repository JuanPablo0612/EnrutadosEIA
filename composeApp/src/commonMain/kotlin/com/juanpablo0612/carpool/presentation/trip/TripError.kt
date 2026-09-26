package com.juanpablo0612.carpool.presentation.trip

sealed class TripError {
    data object TripNotFound : TripError()
    data object NoVehicleSelected : TripError()
    data object UserNotAuthenticated : TripError()
    data object DepartureInPast : TripError()
    data object OriginDestinationRequired : TripError()
    data object SameOriginDestination : TripError()
    data object DepartureTooSoon : TripError()
    data object DepartureTooFar : TripError()
    data object SeatsOutOfRange : TripError()
    data object ContributionOutOfRange : TripError()
    data object MessageTooLong : TripError()
    data object RouteNameRequired : TripError()
    data object RouteSavedButTripFailed : TripError()
    data object NothingSelected : TripError()
    data object Unknown : TripError()
}
