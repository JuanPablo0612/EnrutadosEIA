package com.juanpablo0612.carpool.presentation.bookings.driver

sealed class BookingRequestsEvent {
    data class NavigateToPassengerProfile(val passengerId: String) : BookingRequestsEvent()
}
