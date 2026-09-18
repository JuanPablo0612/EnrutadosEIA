package com.juanpablo0612.carpool.presentation.place.picker

import com.juanpablo0612.carpool.domain.place.model.Coordinates

data class MapPickerUiState(
    val pickedCoordinates: Coordinates,
    val pickedPlaceName: String? = null,
    val isLoadingLocation: Boolean = false,
    val isMyLocationEnabled: Boolean = false,
    val resolvedAddress: String? = null,
    val isResolvingAddress: Boolean = false,
)
