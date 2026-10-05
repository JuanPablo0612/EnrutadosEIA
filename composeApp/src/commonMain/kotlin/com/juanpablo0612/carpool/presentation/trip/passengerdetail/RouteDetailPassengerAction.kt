package com.juanpablo0612.carpool.presentation.trip.passengerdetail

sealed class RouteDetailPassengerAction {
    data object OnBackClick : RouteDetailPassengerAction()
    data object OnOpenConfirmSheet : RouteDetailPassengerAction()
    data object OnDismissConfirmSheet : RouteDetailPassengerAction()
    data class OnPassengerMessageChanged(val message: String) : RouteDetailPassengerAction()

    /** A suggested phrase tapped under the message field; appended to what's already typed. */
    data class OnQuickMessage(val text: String) : RouteDetailPassengerAction()
    data object OnConfirmBookingRequest : RouteDetailPassengerAction()
    data object OnOpenDriverProfile : RouteDetailPassengerAction()

    /** From a trip that no longer takes bookings, back to Buscar for another one. */
    data object OnSearchAnotherTrip : RouteDetailPassengerAction()
}
