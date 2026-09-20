package com.juanpablo0612.carpool.presentation.booking.passenger

sealed class PassengerBookingsEvent {
    data object NavigateBack : PassengerBookingsEvent()
    data class NavigateToTripTracking(val tripId: String) : PassengerBookingsEvent()
    data class NavigateToRating(
        val bookingId: String,
        val tripId: String,
        val rateeId: String,
        val rateeName: String
    ) : PassengerBookingsEvent()
    data object NavigateToSearchTrips : PassengerBookingsEvent()
    data class NavigateToChat(
        val bookingId: String,
        val tripId: String,
        val otherPartyName: String,
        val isReadOnly: Boolean
    ) : PassengerBookingsEvent()
}
