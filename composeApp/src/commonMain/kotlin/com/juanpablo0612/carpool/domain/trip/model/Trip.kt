package com.juanpablo0612.carpool.domain.trip.model

import com.juanpablo0612.carpool.domain.places.model.Place

data class Trip(
    val id: String = "",
    val routeId: String,
    val driverId: String,
    val vehicleId: String,
    val origin: Place,
    val destination: Place,
    val waypoints: List<Place>,
    val departureTime: Long,
    val status: TripStatus
)
