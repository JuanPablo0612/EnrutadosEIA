package com.juanpablo0612.carpool.presentation.mytrips

import com.juanpablo0612.carpool.presentation.rating.RatingTarget

sealed class MyTripsEvent {
    data class NavigateToTripDetail(val tripId: String) : MyTripsEvent()
    data class NavigateToPassengers(val tripId: String) : MyTripsEvent()
    data class NavigateToTracking(val tripId: String) : MyTripsEvent()
    data class NavigateToEditTrip(val tripId: String) : MyTripsEvent()
    data object NavigateToRequests : MyTripsEvent()
    data object NavigateToSearch : MyTripsEvent()
    data object NavigateToPublish : MyTripsEvent()
    data class NavigateToChat(
        val bookingId: String,
        val tripId: String,
        val otherPartyName: String,
        val isReadOnly: Boolean,
    ) : MyTripsEvent()
    data class NavigateToRating(val target: RatingTarget) : MyTripsEvent()
}
