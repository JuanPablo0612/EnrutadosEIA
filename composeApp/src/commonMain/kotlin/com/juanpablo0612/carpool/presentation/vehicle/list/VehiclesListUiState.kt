package com.juanpablo0612.carpool.presentation.vehicle.list

import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle

data class VehiclesListUiState(
    val isLoading: Boolean = true,
    val vehicles: List<Vehicle> = emptyList(),
    val vehicleToDelete: Vehicle? = null,
    val deleteBlockedVehicle: Vehicle? = null,
)
