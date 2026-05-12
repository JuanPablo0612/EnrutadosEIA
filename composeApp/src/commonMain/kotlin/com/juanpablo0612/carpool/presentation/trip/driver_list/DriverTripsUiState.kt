package com.juanpablo0612.carpool.presentation.trip.driver_list

import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle

data class TripWithStats(
    val trip: Trip,
    val occupiedSeats: Int,
    val vehicle: Vehicle?,
)

sealed class TripsTab {
    data object Upcoming : TripsTab()
    data object Past : TripsTab()
}

data class DriverTripsUiState(
    val isLoading: Boolean = true,
    val tab: TripsTab = TripsTab.Upcoming,
    val trips: List<TripWithStats> = emptyList(),
    val pendingCancelTripId: String? = null,
    val error: String? = null,
)
