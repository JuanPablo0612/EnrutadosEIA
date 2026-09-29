package com.juanpablo0612.carpool.domain.booking.model

/**
 * Where the passenger joins or leaves the trip, by the stop's name, so the driver knows where to
 * stop without reconstructing the passenger's search.
 */
data class BookingMeetingStop(
    val name: String,
    /** True when the passenger gets off here (leaving campus) rather than on. */
    val isDropoff: Boolean,
)
