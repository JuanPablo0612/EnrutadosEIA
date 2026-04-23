package com.juanpablo0612.carpool.presentation.places.selector

import com.juanpablo0612.carpool.domain.places.model.Place

sealed class PlaceSelectorAction {
    data class OnQueryChange(val query: String) : PlaceSelectorAction()
    data class OnPlaceSelected(val place: Place) : PlaceSelectorAction()
    data object OnDismiss : PlaceSelectorAction()
}
