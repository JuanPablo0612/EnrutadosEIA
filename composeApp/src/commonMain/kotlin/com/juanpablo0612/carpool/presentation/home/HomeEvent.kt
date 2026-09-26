package com.juanpablo0612.carpool.presentation.home

import com.juanpablo0612.carpool.presentation.mytrips.MyTripsTab

sealed class HomeEvent {
    data object NavigateToPublishTrip : HomeEvent()
    data object NavigateToCreateRoute : HomeEvent()
    data object NavigateToRegisterVehicle : HomeEvent()
    data object NavigateToRoutesList : HomeEvent()
    /** [tab] null keeps whichever tab the user last had open. */
    data class NavigateToMyTrips(val tab: MyTripsTab?) : HomeEvent()
    data object NavigateToDriverBookingRequests : HomeEvent()
    data object NavigateToSearchTrips : HomeEvent()
    data object NavigateToSavedPlaces : HomeEvent()
    data class NavigateToTripDetail(val tripId: String) : HomeEvent()
}
