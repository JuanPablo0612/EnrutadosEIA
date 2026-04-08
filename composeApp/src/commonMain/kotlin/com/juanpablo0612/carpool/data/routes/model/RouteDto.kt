package com.juanpablo0612.carpool.data.routes.model

import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import kotlinx.serialization.Serializable

@Serializable
data class RouteDto(
    val id: String = "",
    val driverId: String,
    val origin: PlaceDto,
    val destination: PlaceDto,
    val waypoints: List<PlaceDto>,
    val targetTime: String, // HH:mm
    val daysOfWeek: List<String>, // MONDAY, TUESDAY...
    val type: String // "TO_UNIVERSITY" or "FROM_UNIVERSITY"
) {
    companion object {
        fun fromDomain(route: Route): RouteDto = RouteDto(
            id = route.id,
            driverId = route.driverId,
            origin = PlaceDto.fromDomain(route.origin),
            destination = PlaceDto.fromDomain(route.destination),
            waypoints = route.waypoints.map { PlaceDto.fromDomain(it) },
            targetTime = route.targetTime.toString(),
            daysOfWeek = route.daysOfWeek.map { it.name },
            type = if (route.type is RouteType.ToUniversity) "TO_UNIVERSITY" else "FROM_UNIVERSITY"
        )
    }
}
