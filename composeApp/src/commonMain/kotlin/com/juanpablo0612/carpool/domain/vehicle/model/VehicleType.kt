package com.juanpablo0612.carpool.domain.vehicle.model

sealed class VehicleType {
    data object Sedan : VehicleType()
    data object Hatchback : VehicleType()
    data object SUV : VehicleType()
    data object Pickup : VehicleType()
    data object Other : VehicleType()
}
