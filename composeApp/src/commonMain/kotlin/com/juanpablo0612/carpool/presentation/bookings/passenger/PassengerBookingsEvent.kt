package com.juanpablo0612.carpool.presentation.bookings.passenger

sealed class PassengerBookingsEvent {
    data object NavigateBack : PassengerBookingsEvent()
    data class NavigateToTripTracking(val tripId: String) : PassengerBookingsEvent()
    data class NavigateToRating(
        val bookingId: String,
        val tripId: String,
        val rateeId: String,
        val rateeName: String
    ) : PassengerBookingsEvent()
}
