package com.juanpablo0612.carpool.presentation.bookings.driver

import com.juanpablo0612.carpool.domain.booking.model.BookingWithPassenger
import com.juanpablo0612.carpool.domain.booking.model.RejectReason

data class BookingRequestsUiState(
    val isLoading: Boolean = true,
    val tab: BookingsTab = BookingsTab.Pending,
    val pending: List<BookingWithPassenger> = emptyList(),
    val confirmed: List<BookingWithPassenger> = emptyList(),
    val history: List<BookingWithPassenger> = emptyList(),
    val processingIds: Set<String> = emptySet(),
    val pendingRejectionFor: String? = null,
    val selectedRejectReason: RejectReason? = null,
    val rejectComment: String = "",
    val cancelConfirmFor: String? = null,
    val snackbarMessage: String? = null,
)
