package com.juanpablo0612.carpool.presentation.trip

import com.juanpablo0612.carpool.domain.trip.model.TripVehicle

/** "Mazda 3 · Gris", or null when the trip carries no vehicle snapshot. */
fun TripVehicle.description(): String? =
    listOf("$brand $model".trim(), color).filter { it.isNotBlank() }.joinToString(" · ").ifBlank { null }
