package com.juanpablo0612.carpool.data.vehicles.model

import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
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
    val photoUrl: String = ""
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
        photoUrl = photoUrl
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
            photoUrl = vehicle.photoUrl
        )
    }
}
