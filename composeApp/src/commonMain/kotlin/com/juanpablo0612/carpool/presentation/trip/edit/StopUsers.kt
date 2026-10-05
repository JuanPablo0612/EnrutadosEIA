package com.juanpablo0612.carpool.presentation.trip.edit

import com.juanpablo0612.carpool.domain.booking.model.Booking

/** Who meets the trip at a stop, for the tag on a stop the driver can't remove. */
sealed class StopUsers {
    /** A single passenger, by first name, getting on or (with [isDropoff]) off there. */
    data class One(val firstName: String, val isDropoff: Boolean) : StopUsers()

    data class Several(val count: Int) : StopUsers()
}

/**
 * The stops, by name as bookings store them, where an open booking meets the trip. A request
 * still pending counts too: the passenger chose the stop, and the driver may yet accept it.
 */
internal fun stopUsersByName(bookings: List<Booking>): Map<String, StopUsers> =
    bookings
        .filter { !it.status.isTerminal && it.meetingStop != null }
        .groupBy { it.meetingStop!!.name }
        .mapValues { (_, atStop) ->
            val passengers = atStop.distinctBy { it.passengerId }
            val only = passengers.singleOrNull()
            val firstName = only?.passengerName?.trim()?.substringBefore(' ').orEmpty()
            if (only != null && firstName.isNotEmpty()) {
                StopUsers.One(firstName = firstName, isDropoff = only.meetingStop!!.isDropoff)
            } else {
                StopUsers.Several(count = passengers.size)
            }
        }
