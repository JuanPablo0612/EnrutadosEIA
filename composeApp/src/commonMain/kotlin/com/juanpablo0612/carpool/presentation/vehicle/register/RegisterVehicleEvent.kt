package com.juanpablo0612.carpool.presentation.vehicle.register

sealed class RegisterVehicleEvent {
    /** A new vehicle was stored under [vehicleId]; the opener may want to select it. */
    data class VehicleRegistered(val vehicleId: String) : RegisterVehicleEvent()
    data object VehicleUpdated : RegisterVehicleEvent()
    data object NavigateBack : RegisterVehicleEvent()
}
