package com.juanpablo0612.carpool.presentation.place.selector

import com.juanpablo0612.carpool.domain.place.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.place.model.Place

data class PlaceSelectorUiState(
    val mode: PlaceSelectorMode = PlaceSelectorMode.Origin,
    val searchQuery: String = "",
    val savedPlaces: List<Place> = emptyList(),
    val communityPlaces: List<Place> = emptyList(),
    val campusPlaces: List<Place> = Place.campusPresets,
    val currentLocation: Place? = null,
    val isResolvingLocation: Boolean = false,
    // Overwritten with the real OS grant state as soon as the ViewModel initializes; false
    // by default so the UI never assumes a permission it hasn't checked.
    val locationPermissionGranted: Boolean = false,
    val searchResults: List<AutocompleteSuggestion> = emptyList(),
    val isSearching: Boolean = false,
    val isResolvingSuggestion: Boolean = false,
    val isConfirmingDelete: Place? = null,
    val error: PlaceSelectorError? = null,
    /** Set while the user is choosing a point on the map instead of from the list. */
    val mapPick: MapPickRequest? = null,
)

/**
 * An open "choose on map" view, starting at the given point. [session] grows with each opening,
 * so the host can give every opening a fresh map instead of the last one's pin.
 */
data class MapPickRequest(
    val session: Int,
    val initialLatitude: Double,
    val initialLongitude: Double,
)
