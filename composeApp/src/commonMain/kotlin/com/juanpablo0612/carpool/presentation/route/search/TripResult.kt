package com.juanpablo0612.carpool.presentation.route.search

import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle

data class TripResult(
    val trip: Trip,
    val vehicle: Vehicle?,
    val availableSeats: Int,
    val driver: PublicProfile? = null
) {
    /**
     * Formatted contribution amount (with thousands separators), or null when the trip is free
     * (no contribution set or contribution is zero/negative). The UI layer is responsible for
     * picking the localized "free" label when this is null.
     */
    val formattedContribution: String?
        get() {
            val amount = trip.contributionPerPassenger
            return if (amount != null && amount > 0) amount.toFormattedAmount() else null
        }
}

private fun Int.toFormattedAmount(): String {
    val s = this.toString()
    val result = StringBuilder()
    s.forEachIndexed { i, c ->
        if (i > 0 && (s.length - i) % 3 == 0) result.append('.')
        result.append(c)
    }
    return result.toString()
}
