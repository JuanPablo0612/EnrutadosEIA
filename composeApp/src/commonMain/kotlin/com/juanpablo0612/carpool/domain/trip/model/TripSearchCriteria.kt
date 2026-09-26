package com.juanpablo0612.carpool.domain.trip.model

import com.juanpablo0612.carpool.domain.place.model.Place

/**
 * What a passenger is looking for. [origin] and [destination] are each optional; a missing one
 * doesn't constrain the match. [maxWalkMeters] is the straight-line distance the passenger accepts
 * between their own place and a driver's stop, applied to both ends.
 */
data class TripSearchCriteria(
    val origin: Place?,
    val destination: Place?,
    val departureAroundEpochMs: Long? = null,
    val toleranceMinutes: Int = 30,
    val maxWalkMeters: Int = DEFAULT_MAX_WALK_METERS,
    val maxContribution: Int? = null,
) {
    companion object {
        /** Roughly a 15-minute walk once street layout and Envigado's slopes are accounted for. */
        const val DEFAULT_MAX_WALK_METERS = 1_000
        val WALK_RADIUS_OPTIONS_METERS = listOf(500, 1_000, 2_000, 3_000)
    }
}
