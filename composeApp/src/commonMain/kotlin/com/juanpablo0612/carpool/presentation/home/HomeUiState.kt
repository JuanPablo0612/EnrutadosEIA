package com.juanpablo0612.carpool.presentation.home

import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.trip.model.Trip

data class HomeUiState(
    val user: User? = null,
    val role: UserRole = UserRole.Driver,
    val isDualRole: Boolean = false,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val nextTrip: Trip? = null,
    val nextBooking: Booking? = null,
    val pendingRequests: List<Booking> = emptyList(),
    val hasVehicles: Boolean = false,
    val hasRoutes: Boolean = false,
    val tripsThisMonth: Int = 0,
    val passengersThisMonth: Int = 0,
    val error: String? = null,
)
