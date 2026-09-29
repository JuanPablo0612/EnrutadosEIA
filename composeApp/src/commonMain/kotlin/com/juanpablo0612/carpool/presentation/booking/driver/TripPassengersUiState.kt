package com.juanpablo0612.carpool.presentation.booking.driver

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.presentation.booking.BookingError
import com.juanpablo0612.carpool.presentation.booking.driver.decision.BookingDecisionsState

data class TripPassengersUiState(
    val isLoading: Boolean = true,
    val loadError: BookingError? = null,
    val trip: Trip? = null,
    val pending: List<Booking> = emptyList(),
    val confirmed: List<Booking> = emptyList(),
    val decisions: BookingDecisionsState = BookingDecisionsState(),
) {
    /** Requests are answered, and seats taken back, only until the trip starts. */
    val isOpen: Boolean get() = trip?.status == TripStatus.Active

    /** Once the trip is over the driver rates its passengers, and chats become read-only. */
    val isFinished: Boolean get() = trip?.status == TripStatus.Completed || trip?.status == TripStatus.Cancelled

    val freeSeats: Int? get() = trip?.let { (it.seatCount - it.confirmedSeats).coerceAtLeast(0) }
}
