package com.juanpablo0612.carpool.presentation.routes.passenger_detail

import com.juanpablo0612.carpool.domain.booking.model.BookingError
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle

data class RouteDetailPassengerUiState(
    val isLoading: Boolean = true,
    val trip: Trip? = null,
    val vehicle: Vehicle? = null,
    val availableSeats: Int = 0,
    val isBooking: Boolean = false,
    val error: BookingError? = null
)
