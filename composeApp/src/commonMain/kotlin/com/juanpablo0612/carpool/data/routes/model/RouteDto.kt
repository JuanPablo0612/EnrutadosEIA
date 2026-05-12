package com.juanpablo0612.carpool.data.routes.model

import com.juanpablo0612.carpool.data.places.model.PlaceDto
import com.juanpablo0612.carpool.domain.routes.model.Route
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
    val name: String = "",
    val recurringDays: List<String> = emptyList(),
    val typicalDepartureTime: String? = null
) {
    fun toDomain(): Route = Route(
        id = id,
        driverId = driverId,
        origin = origin.toDomain(),
        destination = destination.toDomain(),
        waypoints = waypoints.map { it.toDomain() },
        name = name,
        recurringDays = recurringDays.mapNotNull { runCatching { DayOfWeek.valueOf(it) }.getOrNull() }.toSet(),
        typicalDepartureTime = typicalDepartureTime?.let { s ->
            runCatching {
                val parts = s.split(":")
                LocalTime(parts[0].toInt(), parts[1].toInt())
            }.getOrNull()
        }
    )

    companion object {
        fun fromDomain(route: Route): RouteDto = RouteDto(
            id = route.id,
            driverId = route.driverId,
            origin = PlaceDto.fromDomain(route.origin),
            destination = PlaceDto.fromDomain(route.destination),
            waypoints = route.waypoints.map { PlaceDto.fromDomain(it) },
            name = route.name,
            recurringDays = route.recurringDays.map { it.name },
            typicalDepartureTime = route.typicalDepartureTime?.let { t ->
                "${t.hour.toString().padStart(2, '0')}:${t.minute.toString().padStart(2, '0')}"
            }
        )
    }
}
