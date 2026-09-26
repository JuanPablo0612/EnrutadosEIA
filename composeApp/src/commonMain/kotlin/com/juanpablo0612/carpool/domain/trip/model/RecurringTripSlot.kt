package com.juanpablo0612.carpool.domain.trip.model

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

/** One day a recurring route could run on, and whether it can still be published. */
data class RecurringTripSlot(
    val date: LocalDate,
    val departure: Instant,
    val status: SlotStatus,
)

sealed class SlotStatus {
    /** Can be published. */
    data object Available : SlotStatus()

    /** Departs too soon (or already left) to publish. */
    data object Passed : SlotStatus()

    /** The driver already has a live trip on this route that day. */
    data class AlreadyPublished(val tripId: String) : SlotStatus()

    /** Another live trip of the driver departs close to this one; allowed, but flagged. */
    data class Conflict(val tripId: String, val departure: Instant) : SlotStatus()

    val isPublishable: Boolean get() = this is Available || this is Conflict
}
