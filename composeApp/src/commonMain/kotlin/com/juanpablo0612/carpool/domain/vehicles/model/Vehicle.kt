package com.juanpablo0612.carpool.domain.vehicles.model

import kotlinx.datetime.LocalDate

data class Vehicle(
    val id: String = "",
    val driverId: String,
    val brand: String,
    val model: String,
    val licensePlate: String,
    val color: String,
    val year: Int,
    val seatsAvailable: Int,
    val photoUrl: String = "",
    val isPrimary: Boolean = false,
    val type: VehicleType? = null,
    val soatExpiresOn: LocalDate? = null,
    val tecnomecanicaExpiresOn: LocalDate? = null,
)
