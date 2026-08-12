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
