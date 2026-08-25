package com.juanpablo0612.carpool.presentation.trip.passengerdetail

sealed class RouteDetailPassengerEvent {
    data object NavigateBack : RouteDetailPassengerEvent()
    data object BookingCreated : RouteDetailPassengerEvent()
    data object NavigateToPassengerBookings : RouteDetailPassengerEvent()
}
