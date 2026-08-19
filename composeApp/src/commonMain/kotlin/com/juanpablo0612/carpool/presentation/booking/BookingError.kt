package com.juanpablo0612.carpool.presentation.booking

sealed class BookingError {
    data object NotAuthenticated : BookingError()
    data object NoSeatsAvailable : BookingError()
    data object AlreadyBooked : BookingError()
    data object VehicleNotFound : BookingError()
    data object Unknown : BookingError()
}
