package com.juanpablo0612.carpool.presentation.trip.driver_list

sealed class DriverTripsAction {
    data class SelectTab(val tab: TripsTab) : DriverTripsAction()
    data class StartTrip(val tripId: String) : DriverTripsAction()
    data class FinishTrip(val tripId: String) : DriverTripsAction()
    data class CancelTrip(val tripId: String) : DriverTripsAction()
    data class ConfirmCancel(val tripId: String) : DriverTripsAction()
    data object DismissCancel : DriverTripsAction()
    data class OpenTrip(val tripId: String) : DriverTripsAction()
    data class OpenPassengers(val tripId: String) : DriverTripsAction()
    data object PublishTrip : DriverTripsAction()
    data object Refresh : DriverTripsAction()
    data object OnBackClick : DriverTripsAction()
}
