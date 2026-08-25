package com.juanpablo0612.carpool.presentation.trip

sealed class TripError {
    data object TripNotFound : TripError()
    data object NoVehicleSelected : TripError()
    data object UserNotAuthenticated : TripError()
    data object DepartureInPast : TripError()
    data object Unknown : TripError()
}
