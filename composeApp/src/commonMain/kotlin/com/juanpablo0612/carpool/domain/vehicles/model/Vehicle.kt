package com.juanpablo0612.carpool.domain.vehicles.model

data class Vehicle(
    val id: String = "",
    val driverId: String,
    val brand: String,
    val model: String,
    val licensePlate: String,
    val color: String,
    val year: Int,
    val seatsAvailable: Int,
    val photoUrl: String = ""
)
