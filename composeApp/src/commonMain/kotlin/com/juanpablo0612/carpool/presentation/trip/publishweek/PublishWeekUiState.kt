package com.juanpablo0612.carpool.presentation.trip.publishweek

import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.trip.model.RecurringTripSlot
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.trip.TripError
import kotlinx.datetime.LocalDate

data class PublishWeekUiState(
    val isLoading: Boolean = true,
    val route: Route? = null,
    val slots: List<RecurringTripSlot> = emptyList(),
    val selectedDates: Set<LocalDate> = emptySet(),
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: String? = null,
    val seatCount: Int = 1,
    val contributionPerPassenger: Int? = null,
    val message: String = "",
    val isPublishing: Boolean = false,
    val error: TripError? = null,
) {
    val selectedVehicle: Vehicle? get() = vehicles.find { it.id == selectedVehicleId }

    val scheduleMissing: Boolean
        get() = route != null && (route.recurringDays.isEmpty() || route.typicalDepartureTime == null)

    val nothingToPublish: Boolean get() = slots.none { it.status.isPublishable }

    val selectedCount: Int get() = slots.count { it.date in selectedDates && it.status.isPublishable }
}
