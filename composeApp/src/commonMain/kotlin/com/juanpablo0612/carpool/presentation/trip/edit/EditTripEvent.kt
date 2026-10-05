package com.juanpablo0612.carpool.presentation.trip.edit

sealed class EditTripEvent {
    data object NavigateBack : EditTripEvent()
    data object TripUpdated : EditTripEvent()
}
