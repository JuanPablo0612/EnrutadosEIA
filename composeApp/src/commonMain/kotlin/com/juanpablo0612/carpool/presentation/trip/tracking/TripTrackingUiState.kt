package com.juanpablo0612.carpool.presentation.trip.tracking

import com.juanpablo0612.carpool.domain.trip.model.Trip

data class TripTrackingUiState(
    val trip: Trip? = null,
    val passengers: List<PassengerWithStatus> = emptyList(),
    val isDriver: Boolean = false,
    val currentPassengerBookingId: String = "",
    val isLoading: Boolean = true,
    val isCompletingTrip: Boolean = false,
    val showCompleteTripDialog: Boolean = false,
    val showSosDialog: Boolean = false
) {
    val driverLatitude: Double? get() = trip?.driverLatitude
    val driverLongitude: Double? get() = trip?.driverLongitude
    val allDroppedOff: Boolean get() = passengers.isNotEmpty() &&
            passengers.all { it.status is com.juanpablo0612.carpool.domain.trip.model.PickupStatus.DroppedOff }
}
