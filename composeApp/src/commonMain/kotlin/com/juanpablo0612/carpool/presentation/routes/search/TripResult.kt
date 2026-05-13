package com.juanpablo0612.carpool.presentation.routes.search

import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle

data class TripResult(
    val trip: Trip,
    val vehicle: Vehicle?,
    val availableSeats: Int
) {
    val contributionLabel: String
        get() {
            val amount = trip.contributionPerPassenger
            return if (amount != null && amount > 0) "$${amount.toFormattedAmount()}" else "Gratis"
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
