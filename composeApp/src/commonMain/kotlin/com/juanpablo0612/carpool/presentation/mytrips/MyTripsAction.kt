package com.juanpablo0612.carpool.presentation.mytrips

sealed class MyTripsAction {
    data class OnSegmentSelected(val segment: MyTripsSegment) : MyTripsAction()
    data class OnFilterSelected(val filter: MyTripsFilter) : MyTripsAction()
    data class OnItemClick(val item: MyTripItem) : MyTripsAction()
    data object OnOpenRequests : MyTripsAction()
    data object OnSearchTrips : MyTripsAction()
    data object OnPublishTrip : MyTripsAction()

    // Trips the user drives.
    data class OnViewPassengers(val tripId: String) : MyTripsAction()
    data class OnStartTrip(val tripId: String) : MyTripsAction()
    data class OnContinueTrip(val tripId: String) : MyTripsAction()
    data class OnCancelTrip(val tripId: String) : MyTripsAction()

    // Seats the user asked for.
    data class OnMessageDriver(val item: MyTripItem.Riding, val isReadOnly: Boolean) : MyTripsAction()
    data class OnCancelBooking(val item: MyTripItem.Riding) : MyTripsAction()
    data class OnRateDriver(val item: MyTripItem.Riding) : MyTripsAction()

    data object OnConfirm : MyTripsAction()
    data object OnDismissConfirmation : MyTripsAction()
    data object OnDismissError : MyTripsAction()
    data object OnRetry : MyTripsAction()
}
