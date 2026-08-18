package com.juanpablo0612.carpool.presentation.booking.passenger

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingError

data class PassengerBookingsUiState(
    val isLoading: Boolean = true,
    val bookings: List<Booking> = emptyList(),
    val selectedTab: PassengerBookingsTab = PassengerBookingsTab.Upcoming,
    val cancellingBookingId: String? = null,
    val showCancelConfirmFor: String? = null,
    val error: BookingError? = null,
    val successMessage: String? = null
)
