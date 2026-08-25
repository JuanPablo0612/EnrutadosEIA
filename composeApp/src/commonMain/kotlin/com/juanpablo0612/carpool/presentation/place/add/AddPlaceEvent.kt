package com.juanpablo0612.carpool.presentation.place.add

sealed class AddPlaceEvent {
    data object PlaceSaved : AddPlaceEvent()
    data object NavigateBack : AddPlaceEvent()
    data object NavigateToMapPicker : AddPlaceEvent()
}
