package com.juanpablo0612.carpool.domain.trip.model

/**
 * The stop where a trip meets the passenger, found by the search and carried into the trip detail
 * and the booking. [pathIndex] indexes `[origin, waypoints…, destination]`; [isDropoff]
 * says whether the passenger gets off there (leaving campus) rather than on.
 */
data class TripMeetingStop(
    val pathIndex: Int,
    val isDropoff: Boolean,
)
