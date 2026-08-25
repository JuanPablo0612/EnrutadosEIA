package com.juanpablo0612.carpool.presentation.place.picker

import com.juanpablo0612.carpool.domain.place.model.Coordinates

data class MapPickerUiState(
    val pickedCoordinates: Coordinates,
    val isLoadingLocation: Boolean = false,
    val isMyLocationEnabled: Boolean = false,
)
