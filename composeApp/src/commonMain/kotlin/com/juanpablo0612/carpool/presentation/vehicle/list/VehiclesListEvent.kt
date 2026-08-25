package com.juanpablo0612.carpool.presentation.vehicle.list

sealed class VehiclesListEvent {
    data class NavigateToEditVehicle(val vehicleId: String) : VehiclesListEvent()
    data object NavigateToRegisterVehicle : VehiclesListEvent()
    data object NavigateBack : VehiclesListEvent()
}
