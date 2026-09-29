package com.juanpablo0612.carpool.presentation.route.search

import com.juanpablo0612.carpool.presentation.trip.passengerdetail.TripMeetingStop

sealed class SearchRoutesEvent {
    data class NavigateToTripDetail(val tripId: String, val meetingStop: TripMeetingStop?) : SearchRoutesEvent()
}
