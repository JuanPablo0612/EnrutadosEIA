package com.juanpablo0612.carpool.presentation.places.picker

import com.juanpablo0612.carpool.domain.places.model.Coordinates

data class MapPickerUiState(
    val pickedCoordinates: Coordinates,
    val isLoadingLocation: Boolean = false,
    val isMyLocationEnabled: Boolean = false,
)
