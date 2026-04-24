package com.juanpablo0612.carpool.presentation.trip.driver_list

sealed class DriverTripsAction {
    data object OnBackClick : DriverTripsAction()
}
