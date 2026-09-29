package com.juanpablo0612.carpool.presentation.booking.driver

import com.juanpablo0612.carpool.presentation.rating.RatingTarget

sealed class TripPassengersEvent {
    data class NavigateToPassengerProfile(val passengerId: String) : TripPassengersEvent()
    data class NavigateToChat(
        val bookingId: String,
        val tripId: String,
        val passengerName: String,
        val isReadOnly: Boolean,
    ) : TripPassengersEvent()
    data class NavigateToRating(val target: RatingTarget) : TripPassengersEvent()
}
