package com.juanpablo0612.carpool.presentation.home

import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.trip.model.Trip

data class HomeUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    /** The next trip the user drives. */
    val nextTrip: Trip? = null,
    /** The next confirmed seat the user has as a passenger. */
    val nextBooking: Booking? = null,
    val pendingRequests: List<Booking> = emptyList(),
    val hasVehicles: Boolean = false,
    val hasRoutes: Boolean = false,
    val hasTrips: Boolean = false,
    val tripsThisMonth: Int = 0,
    val passengersThisMonth: Int = 0,
    val error: HomeError? = null,
    val pendingRejectBookingId: String? = null,
    val processingBookingIds: Set<String> = emptySet(),
) {
    /** Whether the user has started driving, so driver shortcuts are worth showing. */
    val showDriverSections: Boolean
        get() = hasVehicles || hasRoutes || hasTrips || pendingRequests.isNotEmpty()
}
