package com.juanpablo0612.carpool.presentation.home

import com.juanpablo0612.carpool.presentation.route.search.SearchShortcut

sealed class HomeEvent {
    /** Opens Buscar, on [shortcut] when there is one. */
    data class NavigateToSearchTrips(val shortcut: SearchShortcut?) : HomeEvent()
    data object NavigateToPublishTrip : HomeEvent()
    data object NavigateToRegisterVehicle : HomeEvent()
    data object NavigateToRequests : HomeEvent()
    data object NavigateToNotifications : HomeEvent()
    data class NavigateToTripDetail(val tripId: String) : HomeEvent()
    data class NavigateToPassengers(val tripId: String) : HomeEvent()
}
