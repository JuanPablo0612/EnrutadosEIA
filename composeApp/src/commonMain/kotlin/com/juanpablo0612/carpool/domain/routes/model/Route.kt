package com.juanpablo0612.carpool.domain.routes.model

import com.juanpablo0612.carpool.domain.places.model.Place
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

data class Route(
    val id: String = "",
    val driverId: String,
    val origin: Place,
    val destination: Place,
    val waypoints: List<Place>,
    val name: String = "",
    val recurringDays: Set<DayOfWeek> = emptySet(),
    val typicalDepartureTime: LocalTime? = null
)
