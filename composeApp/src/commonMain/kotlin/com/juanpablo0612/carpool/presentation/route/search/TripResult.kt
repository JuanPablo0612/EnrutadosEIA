package com.juanpablo0612.carpool.presentation.route.search

import com.juanpablo0612.carpool.domain.trip.model.MatchedStop
import com.juanpablo0612.carpool.domain.trip.model.Trip

/**
 * One search result. Everything shown comes from the trip document itself (including its driver
 * and vehicle snapshot), so a result list costs no reads beyond the trips query.
 */
data class TripResult(
    val trip: Trip,
    val availableSeats: Int,
    /** Where the driver picks the passenger up, when the passenger gave an origin. */
    val pickup: MatchedStop? = null,
    /** Where the driver drops the passenger off, when the passenger gave a destination. */
    val dropoff: MatchedStop? = null,
)
