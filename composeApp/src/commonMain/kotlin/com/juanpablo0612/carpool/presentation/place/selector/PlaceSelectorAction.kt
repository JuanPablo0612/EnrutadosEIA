package com.juanpablo0612.carpool.presentation.place.selector

import com.juanpablo0612.carpool.domain.place.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.place.model.Place

sealed class PlaceSelectorAction {
    data class OnQueryChange(val query: String) : PlaceSelectorAction()
    data object UseCurrentLocation : PlaceSelectorAction()
    data object RequestLocationPermission : PlaceSelectorAction()
    data class OnSuggestionSelected(val suggestion: AutocompleteSuggestion) : PlaceSelectorAction()
    data class OnPlaceSelected(val place: Place) : PlaceSelectorAction()
    data object OnAddPlace : PlaceSelectorAction()
    data object OnDismiss : PlaceSelectorAction()
    data class OnDeletePlace(val place: Place) : PlaceSelectorAction()
    data object OnConfirmDelete : PlaceSelectorAction()
    data object OnCancelDelete : PlaceSelectorAction()
}
