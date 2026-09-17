package com.juanpablo0612.carpool.presentation.booking.driver

sealed class TripPassengersEvent {
    data class NavigateToPassengerProfile(val passengerId: String) : TripPassengersEvent()
    data class NavigateToRating(
        val bookingId: String,
        val tripId: String,
        val rateeId: String,
        val rateeName: String
    ) : TripPassengersEvent()
}
