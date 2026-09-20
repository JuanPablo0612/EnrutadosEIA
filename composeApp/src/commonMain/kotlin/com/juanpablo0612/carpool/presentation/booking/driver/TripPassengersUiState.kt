package com.juanpablo0612.carpool.presentation.booking.driver

import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.presentation.booking.BookingError
import com.juanpablo0612.carpool.presentation.booking.model.BookingWithPassenger

data class TripPassengersUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    // Fetched directly from the trip itself rather than derived from the first pending/confirmed
    // booking, so the trip-context header still renders even when a trip has zero bookings.
    val trip: Trip? = null,
    val pending: List<BookingWithPassenger> = emptyList(),
    val confirmed: List<BookingWithPassenger> = emptyList(),
    val processingIds: Set<String> = emptySet(),
    val pendingRejectionFor: String? = null,
    val selectedRejectReason: RejectReason? = null,
    val rejectComment: String = "",
    val cancelConfirmFor: String? = null,
    val error: BookingError? = null,
)
