package com.juanpablo0612.carpool.presentation.place.add

import com.juanpablo0612.carpool.domain.place.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.model.PlaceType

sealed class AddPlaceAction {
    data class SelectType(val type: PlaceType) : AddPlaceAction()
    data class OnNameChanged(val name: String) : AddPlaceAction()
    data class OnAddressChanged(val text: String) : AddPlaceAction()
    data class SelectSuggestion(val suggestion: AutocompleteSuggestion) : AddPlaceAction()
    data class DragPin(val to: Coordinates) : AddPlaceAction()
    data object OnSaveClick : AddPlaceAction()
    data object OnBackClick : AddPlaceAction()
    data object PickOnMap : AddPlaceAction()
    data class OnMapPickResult(val latitude: Double, val longitude: Double) : AddPlaceAction()
}
