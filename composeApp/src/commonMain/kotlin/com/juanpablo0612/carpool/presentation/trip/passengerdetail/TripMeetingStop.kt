package com.juanpablo0612.carpool.presentation.trip.passengerdetail

/**
 * The stop where a trip meets the passenger, carried from a search result into the trip detail so
 * the route can mark it. [pathIndex] indexes `[origin, waypoints…, destination]`; [isDropoff]
 * says whether the passenger gets off there (leaving campus) rather than on.
 */
data class TripMeetingStop(
    val pathIndex: Int,
    val isDropoff: Boolean,
)
