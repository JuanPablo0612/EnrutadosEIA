package com.juanpablo0612.carpool.presentation.vehicles.list

import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle

data class VehiclesListUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = true
)
