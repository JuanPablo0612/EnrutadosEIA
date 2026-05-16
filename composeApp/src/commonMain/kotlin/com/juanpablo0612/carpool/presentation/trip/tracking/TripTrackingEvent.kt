package com.juanpablo0612.carpool.presentation.trip.tracking

sealed class TripTrackingEvent {
    data object TripCompleted : TripTrackingEvent()
    data object NavigateBack : TripTrackingEvent()
    data class NavigateToChat(val bookingId: String) : TripTrackingEvent()
}
