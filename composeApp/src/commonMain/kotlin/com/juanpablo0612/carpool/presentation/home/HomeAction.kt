package com.juanpablo0612.carpool.presentation.home

sealed class HomeAction {
    data object PublishTrip : HomeAction()
    data object CreateRoute : HomeAction()
    data object SearchTrips : HomeAction()
    data object RegisterVehicle : HomeAction()
    data object OpenAllRequests : HomeAction()
    data object ViewMyTrips : HomeAction()
    data object ViewMyRoutes : HomeAction()
    data object ViewSavedPlaces : HomeAction()
    data object Refresh : HomeAction()
    data class AcceptRequest(val bookingId: String) : HomeAction()
    data class OnRejectRequestClick(val bookingId: String) : HomeAction()
    data object OnConfirmReject : HomeAction()
    data object OnDismissRejectConfirm : HomeAction()
    data class OpenTrip(val tripId: String) : HomeAction()
    data class OpenBooking(val tripId: String) : HomeAction()
    data object DismissBookingActionError : HomeAction()
}
