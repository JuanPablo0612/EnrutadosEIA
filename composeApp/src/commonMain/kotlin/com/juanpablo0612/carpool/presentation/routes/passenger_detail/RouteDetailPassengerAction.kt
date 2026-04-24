package com.juanpablo0612.carpool.presentation.routes.passenger_detail

sealed class RouteDetailPassengerAction {
    data object OnBackClick : RouteDetailPassengerAction()
    data class OnBookClick(val vehicleId: String, val totalSeats: Int) : RouteDetailPassengerAction()
    data object OnDismissError : RouteDetailPassengerAction()
}
