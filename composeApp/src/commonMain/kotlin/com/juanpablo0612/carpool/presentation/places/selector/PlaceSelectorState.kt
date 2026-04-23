package com.juanpablo0612.carpool.presentation.places.selector

import com.juanpablo0612.carpool.domain.places.model.Place

data class PlaceSelectorUiState(
    val query: String = "",
    val savedPlaces: List<Place> = emptyList(),
    val searchResults: List<Place> = emptyList(),
    val isLoading: Boolean = false
)
