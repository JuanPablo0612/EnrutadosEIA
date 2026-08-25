package com.juanpablo0612.carpool.presentation.vehicle.register

sealed class RegisterVehicleEvent {
    data object VehicleRegistered : RegisterVehicleEvent()
    data object NavigateBack : RegisterVehicleEvent()
}
