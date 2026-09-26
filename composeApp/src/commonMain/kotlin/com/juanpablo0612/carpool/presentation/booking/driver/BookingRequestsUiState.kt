package com.juanpablo0612.carpool.presentation.booking.driver

import com.juanpablo0612.carpool.presentation.booking.BookingError
import com.juanpablo0612.carpool.presentation.booking.model.BookingWithPassenger
import com.juanpablo0612.carpool.domain.booking.model.RejectReason

data class BookingRequestsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val tab: DriverBookingsTab = DriverBookingsTab.Pending,
    val historyQuery: String = "",
    val pending: List<BookingWithPassenger> = emptyList(),
    val confirmed: List<BookingWithPassenger> = emptyList(),
    val history: List<BookingWithPassenger> = emptyList(),
    val processingIds: Set<String> = emptySet(),
    val pendingRejectionFor: String? = null,
    val selectedRejectReason: RejectReason? = null,
    val rejectComment: String = "",
    val cancelConfirmFor: String? = null,
    val error: BookingError? = null,
    // Shows TripFilledBanner until the driver dismisses it.
    val tripJustFilled: Boolean = false,
) {
    val filteredHistory: List<BookingWithPassenger>
        get() = if (historyQuery.isBlank()) {
            history
        } else {
            history.filter { it.passenger.name.contains(historyQuery, ignoreCase = true) }
        }
}
