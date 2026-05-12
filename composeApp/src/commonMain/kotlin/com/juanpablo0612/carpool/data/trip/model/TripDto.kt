package com.juanpablo0612.carpool.data.trip.model

import com.juanpablo0612.carpool.data.places.model.PlaceDto
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import kotlinx.serialization.Serializable

@Serializable
data class TripDto(
    val id: String = "",
    val routeId: String = "",
    val driverId: String = "",
    val vehicleId: String = "",
    val origin: PlaceDto = PlaceDto(name = "", address = "", latitude = 0.0, longitude = 0.0),
    val destination: PlaceDto = PlaceDto(name = "", address = "", latitude = 0.0, longitude = 0.0),
    val waypoints: List<PlaceDto> = emptyList(),
    val departureTime: Long = 0L,
    val seatCount: Int = 1,
    val contributionPerPassenger: Int? = null,
    val messageToPassengers: String = "",
    val status: String = "ACTIVE"
) {
    fun toDomain(): Trip = Trip(
        id = id,
        routeId = routeId,
        driverId = driverId,
        vehicleId = vehicleId,
        origin = origin.toDomain(),
        destination = destination.toDomain(),
        waypoints = waypoints.map { it.toDomain() },
        departureTime = departureTime,
        seatCount = seatCount,
        contributionPerPassenger = contributionPerPassenger,
        messageToPassengers = messageToPassengers,
        status = when (status) {
            "IN_PROGRESS" -> TripStatus.InProgress
            "COMPLETED" -> TripStatus.Completed
            "CANCELLED" -> TripStatus.Cancelled
            else -> TripStatus.Active
        }
    )

    companion object {
        fun fromDomain(trip: Trip): TripDto = TripDto(
            id = trip.id,
            routeId = trip.routeId,
            driverId = trip.driverId,
            vehicleId = trip.vehicleId,
            origin = PlaceDto.fromDomain(trip.origin),
            destination = PlaceDto.fromDomain(trip.destination),
            waypoints = trip.waypoints.map { PlaceDto.fromDomain(it) },
            departureTime = trip.departureTime,
            seatCount = trip.seatCount,
            contributionPerPassenger = trip.contributionPerPassenger,
            messageToPassengers = trip.messageToPassengers,
            status = when (trip.status) {
                is TripStatus.Active -> "ACTIVE"
                is TripStatus.InProgress -> "IN_PROGRESS"
                is TripStatus.Completed -> "COMPLETED"
                is TripStatus.Cancelled -> "CANCELLED"
            }
        )
    }
}
