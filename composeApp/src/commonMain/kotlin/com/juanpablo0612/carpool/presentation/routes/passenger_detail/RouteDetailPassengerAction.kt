package com.juanpablo0612.carpool.presentation.routes.passenger_detail

sealed class RouteDetailPassengerAction {
    data object OnBackClick : RouteDetailPassengerAction()
    data object OnBookClick : RouteDetailPassengerAction()       // alias for OnOpenConfirmSheet
    data object OnOpenConfirmSheet : RouteDetailPassengerAction()
    data object OnDismissConfirmSheet : RouteDetailPassengerAction()
    data class OnPassengerMessageChanged(val message: String) : RouteDetailPassengerAction()
    data object OnConfirmBookingRequest : RouteDetailPassengerAction()
    data object OnDismissError : RouteDetailPassengerAction()
}
