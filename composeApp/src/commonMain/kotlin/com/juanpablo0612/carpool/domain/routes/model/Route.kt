package com.juanpablo0612.carpool.domain.routes.model

import com.juanpablo0612.carpool.domain.places.model.Place
import kotlinx.datetime.LocalTime
import kotlinx.datetime.DayOfWeek

data class Route(
    val id: String = "",
    val driverId: String,
    val origin: Place,
    val destination: Place,
    val waypoints: List<Place>,
    val targetTime: LocalTime,
    val daysOfWeek: List<DayOfWeek>,
    val type: RouteType
)
