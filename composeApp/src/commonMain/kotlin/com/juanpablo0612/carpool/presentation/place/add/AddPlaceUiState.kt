package com.juanpablo0612.carpool.presentation.place.add

import com.juanpablo0612.carpool.domain.place.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.model.PlaceType

data class AddPlaceUiState(
    val type: PlaceType? = null,
    val name: String = "",
    val address: String = "",
    val coordinates: Coordinates? = null,
    val autocompleteSuggestions: List<AutocompleteSuggestion> = emptyList(),
    val isGeocoding: Boolean = false,
    val isSearchingAddress: Boolean = false,
    val isSaving: Boolean = false,
    val nameError: AddPlaceError? = null,
    val generalError: AddPlaceError? = null,
) {
    val isValid: Boolean get() = name.isNotBlank() && coordinates != null
}
