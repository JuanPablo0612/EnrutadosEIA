package com.juanpablo0612.carpool.presentation.booking.driver

import com.juanpablo0612.carpool.domain.booking.model.BookingError
import com.juanpablo0612.carpool.domain.booking.model.BookingWithPassenger
import com.juanpablo0612.carpool.domain.booking.model.RejectReason

data class BookingRequestsUiState(
    val isLoading: Boolean = true,
    val tab: DriverBookingsTab = DriverBookingsTab.Pending,
    val pending: List<BookingWithPassenger> = emptyList(),
    val confirmed: List<BookingWithPassenger> = emptyList(),
    val history: List<BookingWithPassenger> = emptyList(),
    val processingIds: Set<String> = emptySet(),
    val pendingRejectionFor: String? = null,
    val selectedRejectReason: RejectReason? = null,
    val rejectComment: String = "",
    val cancelConfirmFor: String? = null,
    val error: BookingError? = null,
    // Real state instead of smuggling a control signal through a snackbar string (4.2).
    val tripJustFilled: Boolean = false,
)
