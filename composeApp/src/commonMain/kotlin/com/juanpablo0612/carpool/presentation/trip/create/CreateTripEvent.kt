package com.juanpablo0612.carpool.presentation.trip.create

sealed class CreateTripEvent {
    data object TripPublished : CreateTripEvent()
    data object NavigateBack : CreateTripEvent()
}
