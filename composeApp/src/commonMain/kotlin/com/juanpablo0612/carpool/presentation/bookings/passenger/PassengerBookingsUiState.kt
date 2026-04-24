package com.juanpablo0612.carpool.presentation.bookings.passenger

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingError

data class PassengerBookingsUiState(
    val isLoading: Boolean = true,
    val bookings: List<Booking> = emptyList(),
    val error: BookingError? = null
)
