package com.juanpablo0612.carpool.presentation.booking.driver

import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import com.juanpablo0612.carpool.presentation.booking.BookingError
import com.juanpablo0612.carpool.presentation.booking.model.BookingWithPassenger

data class TripPassengersUiState(
    val isLoading: Boolean = true,
    val pending: List<BookingWithPassenger> = emptyList(),
    val confirmed: List<BookingWithPassenger> = emptyList(),
    val processingIds: Set<String> = emptySet(),
    val pendingRejectionFor: String? = null,
    val selectedRejectReason: RejectReason? = null,
    val rejectComment: String = "",
    val cancelConfirmFor: String? = null,
    val error: BookingError? = null,
)
