package com.juanpablo0612.carpool.presentation.route.search

import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.trip.model.MatchedStop
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle

data class TripResult(
    val trip: Trip,
    val vehicle: Vehicle?,
    val availableSeats: Int,
    val driver: PublicProfile? = null,
    val driverAverageRating: Double? = null,
    /** Where the driver picks the passenger up, when the passenger gave an origin. */
    val pickup: MatchedStop? = null,
    /** Where the driver drops the passenger off, when the passenger gave a destination. */
    val dropoff: MatchedStop? = null,
)
