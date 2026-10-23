package com.juanpablo0612.carpool.presentation.trip.publishweek

import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.trip.model.RecurringTripSlot
import com.juanpablo0612.carpool.domain.trip.model.SlotStatus
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import kotlin.time.Instant

/** What publishing the week will do, gathered for the review sheet. Formatting is left to it. */
data class PublishWeekSummary(
    val route: Route,
    /** The days that get a trip, in date order. A conflicting one carries its [SlotStatus.Conflict]. */
    val trips: List<RecurringTripSlot>,
    /** The days in the window that won't get a trip, and why. */
    val skipped: List<SkippedSlot>,
    val vehicle: Vehicle?,
    val seatCount: Int,
    val contributionPerPassenger: Int?,
    val message: String,
)

data class SkippedSlot(val slot: RecurringTripSlot, val reason: SkipReason)

sealed class SkipReason {
    /** Publishable, but the driver left it unticked. */
    data object NotSelected : SkipReason()

    /** Left unticked because it departs close to another of the driver's trips. */
    data class Conflict(val otherDeparture: Instant) : SkipReason()
    data object AlreadyPublished : SkipReason()
    data object Passed : SkipReason()
}

/** Splits the week's slots into the trips that will be published and the days left out. */
fun PublishWeekUiState.toSummary(): PublishWeekSummary? {
    val route = route ?: return null
    val ordered = slots.sortedBy { it.date }
    val (trips, rest) = ordered.partition { it.status.isPublishable && it.date in selectedDates }
    return PublishWeekSummary(
        route = route,
        trips = trips,
        skipped = rest.map { slot ->
            val reason = when (val status = slot.status) {
                SlotStatus.Available -> SkipReason.NotSelected
                is SlotStatus.Conflict -> SkipReason.Conflict(status.departure)
                is SlotStatus.AlreadyPublished -> SkipReason.AlreadyPublished
                SlotStatus.Passed -> SkipReason.Passed
            }
            SkippedSlot(slot, reason)
        },
        vehicle = selectedVehicle,
        seatCount = seatCount,
        contributionPerPassenger = contributionPerPassenger,
        message = message,
    )
}
