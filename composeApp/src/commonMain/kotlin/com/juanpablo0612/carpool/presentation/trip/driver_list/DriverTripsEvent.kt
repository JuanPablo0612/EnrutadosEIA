package com.juanpablo0612.carpool.presentation.trip.driver_list

sealed class DriverTripsEvent {
    data object NavigateBack : DriverTripsEvent()
}
