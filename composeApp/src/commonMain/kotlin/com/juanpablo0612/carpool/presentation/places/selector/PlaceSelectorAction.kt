package com.juanpablo0612.carpool.presentation.places.selector

import com.juanpablo0612.carpool.domain.places.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.places.model.Place

sealed class PlaceSelectorAction {
    data class OnQueryChange(val query: String) : PlaceSelectorAction()
    data object UseCurrentLocation : PlaceSelectorAction()
    data object RequestLocationPermission : PlaceSelectorAction()
    data class OnSuggestionSelected(val suggestion: AutocompleteSuggestion) : PlaceSelectorAction()
    data class OnPlaceSelected(val place: Place) : PlaceSelectorAction()
    data object OnAddPlace : PlaceSelectorAction()
    data object OnDismiss : PlaceSelectorAction()
}
