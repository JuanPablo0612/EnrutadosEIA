package com.juanpablo0612.carpool.presentation.booking.driver

sealed class BookingRequestsEvent {
    data class NavigateToPassengerProfile(val passengerId: String) : BookingRequestsEvent()
    data class NavigateToTripPassengers(val tripId: String) : BookingRequestsEvent()
    data class NavigateToChat(
        val bookingId: String,
        val tripId: String,
        val passengerName: String,
    ) : BookingRequestsEvent()
}
