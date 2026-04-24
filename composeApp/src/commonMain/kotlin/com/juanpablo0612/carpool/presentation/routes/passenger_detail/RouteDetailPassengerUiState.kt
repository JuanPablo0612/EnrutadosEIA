package com.juanpablo0612.carpool.presentation.routes.passenger_detail

import com.juanpablo0612.carpool.domain.booking.model.BookingError
import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle

data class RouteDetailPassengerUiState(
    val isLoading: Boolean = true,
    val route: Route? = null,
    val vehiclesWithSeats: List<VehicleWithAvailableSeats> = emptyList(),
    val isBooking: Boolean = false,
    val error: BookingError? = null
)

data class VehicleWithAvailableSeats(
    val vehicle: Vehicle,
    val availableSeats: Int
)
