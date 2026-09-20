package com.juanpablo0612.carpool.presentation.trip.driverlist

sealed class DriverTripsAction {
    data class SelectTab(val tab: TripsTab) : DriverTripsAction()
    data class StartTrip(val tripId: String) : DriverTripsAction()
    data class ConfirmStart(val tripId: String) : DriverTripsAction()
    data object DismissStart : DriverTripsAction()
    data class FinishTrip(val tripId: String) : DriverTripsAction()
    data class TrackTrip(val tripId: String) : DriverTripsAction()
    data class CancelTrip(val tripId: String) : DriverTripsAction()
    data class ConfirmCancel(val tripId: String) : DriverTripsAction()
    data object DismissCancel : DriverTripsAction()
    data class ConfirmFinish(val tripId: String) : DriverTripsAction()
    data object DismissFinish : DriverTripsAction()
    data class OpenTrip(val tripId: String) : DriverTripsAction()
    data class OpenPassengers(val tripId: String) : DriverTripsAction()
    data object PublishTrip : DriverTripsAction()
    data object Refresh : DriverTripsAction()
    data class OnPastSearchQueryChanged(val query: String) : DriverTripsAction()
    data object DismissError : DriverTripsAction()
}
