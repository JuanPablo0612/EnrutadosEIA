package com.juanpablo0612.carpool.domain.trip.model

import com.juanpablo0612.carpool.domain.place.model.Place

/**
 * What a passenger is looking for. [origin] and [destination] are each optional; a missing one
 * doesn't constrain the match. [maxWalkMeters] is how far, in a straight line, a driver's stop may
 * be from the passenger's place for the trip to count as passing by; it tunes matching and is not
 * something the passenger chooses or sees.
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

        /**
         * A search to or from [campus]: [place] is where the passenger gets on going
         * [CampusDirection.ToCampus], and where they get off going [CampusDirection.FromCampus].
         */
        fun forCampus(
            direction: CampusDirection,
            campus: Place,
            place: Place?,
            departureAroundEpochMs: Long? = null,
            toleranceMinutes: Int = 30,
        ): TripSearchCriteria = when (direction) {
            CampusDirection.ToCampus -> TripSearchCriteria(
                origin = place,
                destination = campus,
                departureAroundEpochMs = departureAroundEpochMs,
                toleranceMinutes = toleranceMinutes,
            )
            CampusDirection.FromCampus -> TripSearchCriteria(
                origin = campus,
                destination = place,
                departureAroundEpochMs = departureAroundEpochMs,
                toleranceMinutes = toleranceMinutes,
            )
        }
    }
}
