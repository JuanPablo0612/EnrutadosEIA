package com.juanpablo0612.carpool.presentation.trip.driver_list

import com.juanpablo0612.carpool.domain.trip.model.Trip

data class DriverTripsUiState(
    val trips: List<Trip> = emptyList(),
    val isLoading: Boolean = true
)
