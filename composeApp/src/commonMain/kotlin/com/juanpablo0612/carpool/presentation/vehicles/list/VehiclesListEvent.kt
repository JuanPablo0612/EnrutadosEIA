package com.juanpablo0612.carpool.presentation.vehicles.list

sealed class VehiclesListEvent {
    data object NavigateToRegisterVehicle : VehiclesListEvent()
}
