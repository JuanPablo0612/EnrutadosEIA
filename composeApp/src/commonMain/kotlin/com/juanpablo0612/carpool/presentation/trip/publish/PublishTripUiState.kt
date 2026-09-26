package com.juanpablo0612.carpool.presentation.trip.publish

import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.place.stops.SelectionTarget
import com.juanpablo0612.carpool.presentation.place.stops.StopsDraft
import com.juanpablo0612.carpool.presentation.trip.TripError
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class PublishTripUiState(
    val isLoading: Boolean = true,
    /** The saved route this trip starts from; null when publishing from scratch. */
    val linkedRoute: Route? = null,
    val savedRoutes: List<Route> = emptyList(),
    val stops: StopsDraft = StopsDraft(),
    val selectionTarget: SelectionTarget? = null,
    val departureDate: LocalDate? = null,
    val departureTime: LocalTime? = null,
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: String? = null,
    /** Vehicles that existed when the driver left to register one; a new id gets auto-selected. */
    val vehicleIdsBeforeRegister: Set<String>? = null,
    val seatCount: Int = 1,
    val contributionPerPassenger: Int? = null,
    val message: String = "",
    val saveAsRoute: Boolean = false,
    val routeName: String = "",
    val recurringDays: Set<DayOfWeek> = emptySet(),
    /** Validation problems, shown next to the fields they belong to. */
    val fieldErrors: Set<TripError> = emptySet(),
    val error: TripError? = null,
    /** A route saved by a previous attempt whose trip failed; reused on retry. */
    val savedRouteIdPendingTrip: String? = null,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val showSummary: Boolean = false,
    val showDiscardConfirm: Boolean = false,
    val isPublishing: Boolean = false,
) {
    val selectedVehicle: Vehicle? get() = vehicles.find { it.id == selectedVehicleId }

    val isFromScratch: Boolean get() = linkedRoute == null

    val isDirty: Boolean
        get() = (isFromScratch && !stops.isEmpty) || message.isNotBlank() || saveAsRoute ||
            contributionPerPassenger != null
}
