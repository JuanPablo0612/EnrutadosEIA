package com.juanpablo0612.carpool.presentation.booking.driver

import com.juanpablo0612.carpool.domain.booking.model.Booking

/**
 * The open bookings of one of the driver's trips, as "Solicitudes" lists them: the driver answers
 * requests trip by trip, since what they can accept depends on that trip's free seats.
 */
data class TripBookings(
    val tripId: String,
    val departureTime: Long,
    val originName: String,
    val destinationName: String,
    /** Seats still free on the trip, or null while the trip hasn't loaded. */
    val freeSeats: Int?,
    val bookings: List<Booking>,
) {
    val isFull: Boolean get() = freeSeats == 0

    /** One seat left and more than one request after it: accepting one leaves the rest waiting. */
    val isLastSeatContested: Boolean get() = freeSeats == 1 && bookings.size > 1
}

/**
 * Groups [this] by trip, soonest trip first. Within a trip, requests keep their arrival order
 * (first come, first answered) and [freeSeats] supplies each trip's free seats.
 */
fun List<Booking>.groupByTrip(freeSeats: Map<String, Int>): List<TripBookings> =
    groupBy { it.tripId }
        .map { (tripId, bookings) ->
            val first = bookings.first()
            TripBookings(
                tripId = tripId,
                departureTime = first.departureTime,
                originName = first.originName,
                destinationName = first.destinationName,
                freeSeats = freeSeats[tripId],
                bookings = bookings.sortedBy { it.createdAt },
            )
        }
        .sortedBy { it.departureTime }
