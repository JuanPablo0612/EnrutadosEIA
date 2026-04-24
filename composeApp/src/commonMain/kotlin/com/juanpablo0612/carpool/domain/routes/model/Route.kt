package com.juanpablo0612.carpool.domain.routes.model

import com.juanpablo0612.carpool.domain.places.model.Place

data class Route(
    val id: String = "",
    val driverId: String,
    val origin: Place,
    val destination: Place,
    val waypoints: List<Place>
)
