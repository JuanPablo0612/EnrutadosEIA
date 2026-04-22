package com.juanpablo0612.carpool.presentation.places.add

sealed class AddPlaceEvent {
    data object PlaceSaved : AddPlaceEvent()
    data object NavigateBack : AddPlaceEvent()
}
