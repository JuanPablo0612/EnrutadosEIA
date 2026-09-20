package com.juanpablo0612.carpool.presentation.booking.passenger

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.presentation.booking.BookingError

data class PassengerBookingsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val selectedTab: PassengerBookingsTab = PassengerBookingsTab.Upcoming,
    val pastSearchQuery: String = "",
    val cancellingBookingId: String? = null,
    val showCancelConfirmFor: String? = null,
    val error: BookingError? = null,
    /** driverId -> display name, resolved once per distinct driver so the rating sheet can show a real name. */
    val driverNames: Map<String, String> = emptyMap(),
    /** tripId -> "brand model · color", resolved via the trip's vehicleId (Booking has none of its own). */
    val vehicleSummaries: Map<String, String> = emptyMap()
)
