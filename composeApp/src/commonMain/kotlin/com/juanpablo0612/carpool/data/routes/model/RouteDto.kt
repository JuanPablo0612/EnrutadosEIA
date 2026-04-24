package com.juanpablo0612.carpool.data.routes.model

import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.data.places.model.PlaceDto
import kotlinx.serialization.Serializable

@Serializable
data class RouteDto(
    val id: String = "",
    val driverId: String = "",
    val origin: PlaceDto = PlaceDto(name = "", address = "", latitude = 0.0, longitude = 0.0),
    val destination: PlaceDto = PlaceDto(name = "", address = "", latitude = 0.0, longitude = 0.0),
    val waypoints: List<PlaceDto> = emptyList()
) {
    fun toDomain(): Route = Route(
        id = id,
        driverId = driverId,
        origin = origin.toDomain(),
        destination = destination.toDomain(),
        waypoints = waypoints.map { it.toDomain() }
    )

    companion object {
        fun fromDomain(route: Route): RouteDto = RouteDto(
            id = route.id,
            driverId = route.driverId,
            origin = PlaceDto.fromDomain(route.origin),
            destination = PlaceDto.fromDomain(route.destination),
            waypoints = route.waypoints.map { PlaceDto.fromDomain(it) }
        )
    }
}
