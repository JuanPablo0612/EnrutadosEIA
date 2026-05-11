package com.juanpablo0612.carpool.presentation.home

sealed class HomeEvent {
    data object NavigateToSwitchRole : HomeEvent()
    data object NavigateToCreateRoute : HomeEvent()
    data object NavigateToRegisterVehicle : HomeEvent()
    data object NavigateToRoutesList : HomeEvent()
    data object NavigateToDriverTrips : HomeEvent()
    data object NavigateToDriverBookingRequests : HomeEvent()
    data object NavigateToSearchTrips : HomeEvent()
    data object NavigateToPassengerBookings : HomeEvent()
    data class NavigateToTripDetail(val tripId: String) : HomeEvent()
    data class NavigateToTripDetailPassenger(val tripId: String) : HomeEvent()
}
