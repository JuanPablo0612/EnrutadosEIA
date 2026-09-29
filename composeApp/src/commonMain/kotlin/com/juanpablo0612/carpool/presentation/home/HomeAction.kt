package com.juanpablo0612.carpool.presentation.home

sealed class HomeAction {
    data object SearchTrips : HomeAction()
    data object PublishTrip : HomeAction()
    data object RegisterVehicle : HomeAction()
    data object OpenRequests : HomeAction()
    data object OpenNotifications : HomeAction()
    data object Refresh : HomeAction()
    data class OpenTrip(val tripId: String) : HomeAction()
    data class OpenPassengers(val tripId: String) : HomeAction()
}
