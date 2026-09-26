package com.juanpablo0612.carpool.domain.trip.validation

import com.juanpablo0612.carpool.domain.place.model.matchDistanceMeters
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/** Checks a trip before publishing. Pure; `now` is a parameter so it is testable. */
object TripDraftValidator {
    val MIN_LEAD_TIME = 15.minutes
    const val MAX_DAYS_AHEAD = 30

    /**
     * Carpooling in Colombia may only share costs, not turn a profit, so the per-passenger
     * contribution is capped.
     */
    const val MAX_CONTRIBUTION_COP = 50_000
    const val MAX_MESSAGE_LENGTH = 140

    /** Closer than this, origin and destination are the same place. */
    private const val SAME_PLACE_METERS = 100.0

    fun validate(draft: TripDraft, now: Instant): List<TripValidationError> = buildList {
        val origin = draft.origin
        val destination = draft.destination
        if (origin == null || destination == null) {
            add(TripValidationError.OriginDestinationRequired)
        } else if ((matchDistanceMeters(origin, destination) ?: Double.MAX_VALUE) < SAME_PLACE_METERS) {
            add(TripValidationError.SameOriginDestination)
        }

        if (draft.departure < now + MIN_LEAD_TIME) add(TripValidationError.DepartureTooSoon)
        if (draft.departure > now + MAX_DAYS_AHEAD.days) add(TripValidationError.DepartureTooFar)

        val capacity = draft.vehicleCapacity
        if (capacity == null) {
            add(TripValidationError.NoVehicleSelected)
        } else if (draft.seatCount !in 1..capacity) {
            add(TripValidationError.SeatsOutOfRange)
        }

        val contribution = draft.contributionPerPassenger
        if (contribution != null && contribution !in 0..MAX_CONTRIBUTION_COP) {
            add(TripValidationError.ContributionOutOfRange)
        }
        if (draft.message.length > MAX_MESSAGE_LENGTH) add(TripValidationError.MessageTooLong)
        if (draft.routeNameToSave != null && draft.routeNameToSave.isBlank()) {
            add(TripValidationError.RouteNameRequired)
        }
    }
}
