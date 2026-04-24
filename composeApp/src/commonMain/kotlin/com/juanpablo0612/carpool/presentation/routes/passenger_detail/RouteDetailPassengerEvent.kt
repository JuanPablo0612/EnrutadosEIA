package com.juanpablo0612.carpool.presentation.routes.passenger_detail

sealed class RouteDetailPassengerEvent {
    data object NavigateBack : RouteDetailPassengerEvent()
    data object BookingCreated : RouteDetailPassengerEvent()
}
