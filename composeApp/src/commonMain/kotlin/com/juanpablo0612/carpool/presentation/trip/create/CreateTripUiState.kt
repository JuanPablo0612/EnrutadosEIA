package com.juanpablo0612.carpool.presentation.trip.create

import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.trip.model.TripError
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class CreateTripUiState(
    val route: Route? = null,
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: String? = null,
    val departureDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date
        .plus(1, DateTimeUnit.DAY),
    val departureTime: LocalTime = LocalTime(7, 0),
    val seatCount: Int = 1,
    val contributionPerPassenger: Int? = null,
    val messageToPassengers: String = "",
    val isLoading: Boolean = true,
    val isPublishing: Boolean = false,
    val error: TripError? = null,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
) {
    val selectedVehicle: Vehicle?
        get() = vehicles.find { it.id == selectedVehicleId }

    val canPublish: Boolean
        get() = route != null
            && selectedVehicleId != null
            && seatCount > 0
            && messageToPassengers.length <= 140
}
