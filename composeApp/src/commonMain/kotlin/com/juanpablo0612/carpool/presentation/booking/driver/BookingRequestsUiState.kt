package com.juanpablo0612.carpool.presentation.booking.driver

import com.juanpablo0612.carpool.presentation.booking.BookingError
import com.juanpablo0612.carpool.presentation.booking.driver.decision.BookingDecisionsState

sealed class BookingRequestsTab {
    /** Requests waiting on the driver's answer. */
    data object Pending : BookingRequestsTab()

    /** Seats already given on upcoming trips. */
    data object Accepted : BookingRequestsTab()
}

data class BookingRequestsUiState(
    val isLoading: Boolean = true,
    val loadError: BookingError? = null,
    val tab: BookingRequestsTab = BookingRequestsTab.Pending,
    val pending: List<TripBookings> = emptyList(),
    val accepted: List<TripBookings> = emptyList(),
    val decisions: BookingDecisionsState = BookingDecisionsState(),
) {
    val pendingCount: Int get() = pending.sumOf { it.bookings.size }
    val acceptedCount: Int get() = accepted.sumOf { it.bookings.size }
}
