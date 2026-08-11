package com.juanpablo0612.carpool.presentation.places.selector

import com.juanpablo0612.carpool.domain.places.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.places.model.Place

data class PlaceSelectorUiState(
    val mode: PlaceSelectorMode = PlaceSelectorMode.Origin,
    val searchQuery: String = "",
    val savedPlaces: List<Place> = emptyList(),
    val campusPlaces: List<Place> = Place.campusPresets,
    val currentLocation: Place? = null,
    val isResolvingLocation: Boolean = false,
    // Overwritten with the real OS grant state as soon as the ViewModel initializes (3.12); false
    // by default so the UI never assumes a permission it hasn't checked.
    val locationPermissionGranted: Boolean = false,
    val searchResults: List<AutocompleteSuggestion> = emptyList(),
    val isSearching: Boolean = false,
    val isConfirmingDelete: Place? = null,
)
