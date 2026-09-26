package com.juanpablo0612.carpool.presentation.trip.publish

sealed class PublishTripEvent {
    data object TripPublished : PublishTripEvent()
    data object NavigateBack : PublishTripEvent()
    data object NavigateToRegisterVehicle : PublishTripEvent()
}
