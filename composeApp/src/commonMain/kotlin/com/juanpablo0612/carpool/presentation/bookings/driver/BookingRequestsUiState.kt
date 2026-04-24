package com.juanpablo0612.carpool.presentation.bookings.driver

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingError

data class BookingRequestsUiState(
    val isLoading: Boolean = true,
    val requests: List<Booking> = emptyList(),
    val processingIds: Set<String> = emptySet(),
    val error: BookingError? = null
)
