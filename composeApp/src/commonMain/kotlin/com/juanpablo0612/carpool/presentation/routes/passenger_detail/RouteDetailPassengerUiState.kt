package com.juanpablo0612.carpool.presentation.routes.passenger_detail

import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.booking.model.BookingError
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle

data class RouteDetailPassengerUiState(
    val isLoading: Boolean = true,
    val trip: Trip? = null,
    val vehicle: Vehicle? = null,
    val driver: PublicProfile? = null,
    val availableSeats: Int = 0,
    val alreadyRequested: Boolean = false,
    val isBooking: Boolean = false,
    val showConfirmSheet: Boolean = false,
    val passengerMessage: String = "",
    val error: BookingError? = null
)
