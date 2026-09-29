package com.juanpablo0612.carpool.presentation.home

sealed class HomeEvent {
    data object NavigateToSearchTrips : HomeEvent()
    data object NavigateToPublishTrip : HomeEvent()
    data object NavigateToRegisterVehicle : HomeEvent()
    data object NavigateToRequests : HomeEvent()
    data object NavigateToNotifications : HomeEvent()
    data class NavigateToTripDetail(val tripId: String) : HomeEvent()
    data class NavigateToPassengers(val tripId: String) : HomeEvent()
}
