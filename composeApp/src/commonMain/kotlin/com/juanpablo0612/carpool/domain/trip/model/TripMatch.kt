package com.juanpablo0612.carpool.domain.trip.model

import com.juanpablo0612.carpool.domain.place.model.Place

/** A stop on a trip's path, how far it is from the passenger's place, and its index on the path. */
data class MatchedStop(
    val place: Place,
    val pathIndex: Int,
    val distanceMeters: Double,
)

/**
 * A trip that fits a [TripSearchCriteria]. [pickup] is null when the passenger gave no origin and
 * [dropoff] is null when they gave no destination.
 */
data class TripMatch(
    val trip: Trip,
    val pickup: MatchedStop?,
    val dropoff: MatchedStop?,
    val availableSeats: Int,
)

/** How an empty search could be relaxed: how many trips would match at any time of day. */
data class SearchRelaxation(
    val anyTimeCount: Int,
)
