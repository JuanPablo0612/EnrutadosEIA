package com.juanpablo0612.carpool.data.routes.model

import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import com.juanpablo0612.carpool.data.places.model.PlaceDto
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class RouteDto(
    val id: String = "",
    val driverId: String = "",
    val origin: PlaceDto = PlaceDto(name = "", address = "", latitude = 0.0, longitude = 0.0),
    val destination: PlaceDto = PlaceDto(name = "", address = "", latitude = 0.0, longitude = 0.0),
    val waypoints: List<PlaceDto> = emptyList(),
    val targetTime: String = "07:00", // HH:mm
    val daysOfWeek: List<String> = emptyList(), // MONDAY, TUESDAY...
    val type: String = "TO_UNIVERSITY" // "TO_UNIVERSITY" or "FROM_UNIVERSITY"
) {
    fun toDomain(): Route = Route(
        id = id,
        driverId = driverId,
        origin = origin.toDomain(),
        destination = destination.toDomain(),
        waypoints = waypoints.map { it.toDomain() },
        targetTime = LocalTime.parse(targetTime),
        daysOfWeek = daysOfWeek.map { DayOfWeek.valueOf(it) },
        type = if (type == "TO_UNIVERSITY") RouteType.ToUniversity else RouteType.FromUniversity
    )

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
