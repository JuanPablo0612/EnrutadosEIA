package com.juanpablo0612.carpool.domain.trip.model

import com.juanpablo0612.carpool.domain.place.model.Place

data class Trip(
    val id: String = "",
    val routeId: String,
    val driverId: String,
    val vehicleId: String,
    /** Snapshot of the driver at publish time; see [TripDriver]. */
    val driver: TripDriver = TripDriver(),
    /** Snapshot of the car at publish time; see [TripVehicle]. */
    val vehicle: TripVehicle = TripVehicle(),
    val origin: Place,
    val destination: Place,
    val waypoints: List<Place>,
    val departureTime: Long,
    val seatCount: Int = 1,
    val contributionPerPassenger: Int? = null,
    val messageToPassengers: String = "",
    val status: TripStatus,
    val driverLatitude: Double? = null,
    val driverLongitude: Double? = null,
    val passengerStatuses: Map<String, String> = emptyMap(),
    // Denormalized count of CONFIRMED bookings for this trip, maintained by BookingRepositoryImpl
    // whenever a booking transitions into/out of CONFIRMED. Lets any signed-in user compute
    // available seats from the trip document alone, without reading (PII-carrying) booking
    // documents they are not a party to. See GetTripAvailableSeatsUseCase.
    val confirmedSeats: Int = 0,
)

/**
 * Whether a passenger may still ask for a seat on this trip at [now] (epoch millis): it has not
 * started, finished or been cancelled, and its departure is still ahead.
 */
fun Trip.acceptsBookings(now: Long): Boolean = closedReason(now) == null

/**
 * Why this trip no longer takes seat requests at [now] (epoch millis), or null while it still does.
 * An active trip whose departure time has passed counts as departed even before the driver starts
 * it, since no passenger could still make it to the stop.
 */
fun Trip.closedReason(now: Long): TripClosedReason? = when {
    status == TripStatus.Cancelled -> TripClosedReason.Cancelled
    status == TripStatus.Completed -> TripClosedReason.Finished
    status == TripStatus.InProgress || departureTime <= now -> TripClosedReason.Departed
    else -> null
}

/** Why a trip stopped taking seat requests; see [closedReason]. */
sealed class TripClosedReason {
    data object Cancelled : TripClosedReason()
    data object Departed : TripClosedReason()
    data object Finished : TripClosedReason()
}
