package com.juanpablo0612.carpool.presentation.routes.passenger_detail

sealed class RouteDetailPassengerAction {
    data object OnBackClick : RouteDetailPassengerAction()
    data object OnBookClick : RouteDetailPassengerAction()
    data object OnDismissError : RouteDetailPassengerAction()
}
