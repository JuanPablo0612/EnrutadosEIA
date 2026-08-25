package com.juanpablo0612.carpool.data.vehicle.model

import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicle.model.VehicleType
import kotlinx.serialization.Serializable

@Serializable
data class VehicleDto(
    val id: String = "",
    val driverId: String = "",
    val brand: String = "",
    val model: String = "",
    val licensePlate: String = "",
    val color: String = "",
    val year: Int = 2024,
    val seatsAvailable: Int = 1,
    val photoUrl: String = "",
    val isPrimary: Boolean = false,
    val type: String = "",
) {
    fun toDomain(): Vehicle = Vehicle(
        id = id,
        driverId = driverId,
        brand = brand,
        model = model,
        licensePlate = licensePlate,
        color = color,
        year = year,
        seatsAvailable = seatsAvailable,
        photoUrl = photoUrl,
        isPrimary = isPrimary,
        type = type.toVehicleType(),
    )

    companion object {
        fun fromDomain(vehicle: Vehicle): VehicleDto = VehicleDto(
            id = vehicle.id,
            driverId = vehicle.driverId,
            brand = vehicle.brand,
            model = vehicle.model,
            licensePlate = vehicle.licensePlate,
            color = vehicle.color,
            year = vehicle.year,
            seatsAvailable = vehicle.seatsAvailable,
            photoUrl = vehicle.photoUrl,
            isPrimary = vehicle.isPrimary,
            type = vehicle.type.toTypeString(),
        )
    }
}

private fun String.toVehicleType(): VehicleType? = when (this) {
    "Sedan" -> VehicleType.Sedan
    "Hatchback" -> VehicleType.Hatchback
    "SUV" -> VehicleType.SUV
    "Pickup" -> VehicleType.Pickup
    "Other" -> VehicleType.Other
    else -> null
}

private fun VehicleType?.toTypeString(): String = when (this) {
    VehicleType.Sedan -> "Sedan"
    VehicleType.Hatchback -> "Hatchback"
    VehicleType.SUV -> "SUV"
    VehicleType.Pickup -> "Pickup"
    VehicleType.Other -> "Other"
    null -> ""
}

