package com.juanpablo0612.carpool.presentation.home

sealed class HomeAction {
    data object SwitchRole : HomeAction()
    data object PublishTrip : HomeAction()
    data object CreateRoute : HomeAction()
    data object SearchTrips : HomeAction()
    data object RegisterVehicle : HomeAction()
    data object OpenAllRequests : HomeAction()
    data object ViewMyTrips : HomeAction()
    data object ViewMyRoutes : HomeAction()
    data object ViewMyBookings : HomeAction()
    data object ViewSavedPlaces : HomeAction()
    data object Refresh : HomeAction()
    data class AcceptRequest(val bookingId: String) : HomeAction()
    data class RejectRequest(val bookingId: String) : HomeAction()
    data class OpenTrip(val tripId: String) : HomeAction()
    data class OpenBooking(val tripId: String) : HomeAction()
}
