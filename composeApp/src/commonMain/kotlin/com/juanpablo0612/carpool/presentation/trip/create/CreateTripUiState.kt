package com.juanpablo0612.carpool.presentation.trip.create

import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.trip.model.TripError
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class CreateTripUiState(
    val route: Route? = null,
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: String? = null,
    val departureDate: LocalDate? = null,
    val departureTime: LocalTime? = null,
    val isLoading: Boolean = true,
    val isPublishing: Boolean = false,
    val error: TripError? = null,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false
)
