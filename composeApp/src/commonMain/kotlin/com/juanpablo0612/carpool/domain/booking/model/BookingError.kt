package com.juanpablo0612.carpool.domain.booking.model

sealed class BookingError {
    data object NotAuthenticated : BookingError()
    data object NoSeatsAvailable : BookingError()
    data object AlreadyBooked : BookingError()
    data object BookingNotFound : BookingError()
    data object Unauthorized : BookingError()
    data object Unknown : BookingError()
}
