package com.juanpablo0612.carpool.presentation.trip.driver_list

sealed class DriverTripsEvent {
    data object NavigateBack : DriverTripsEvent()
    data object NavigateToRoutesList : DriverTripsEvent()
    data class NavigateToTripDetail(val tripId: String) : DriverTripsEvent()
    data class NavigateToPassengers(val tripId: String) : DriverTripsEvent()
}
