package com.juanpablo0612.carpool.presentation.trip.tracking

import com.juanpablo0612.carpool.domain.trip.model.Trip

data class TripTrackingUiState(
    val trip: Trip? = null,
    val passengers: List<PassengerWithStatus> = emptyList(),
    val isDriver: Boolean = false,
    val currentPassengerBookingId: String = "",
    /** The driver's display name, resolved for the passenger-side chat title. */
    val driverName: String = "",
    val isLoading: Boolean = true,
    val isCompletingTrip: Boolean = false,
    val showCompleteTripDialog: Boolean = false,
    val pendingDropOffPassengerId: String? = null,
    /** passengerId set currently mid-mutation, so their action buttons can be disabled to prevent double-taps. */
    val processingPassengerIds: Set<String> = emptySet(),
    /** True while the driver-location poll loop is actually running (see updateLocationPolling). */
    val isSharingLocation: Boolean = false,
    val error: TripTrackingError? = null
) {
    val driverLatitude: Double? get() = trip?.driverLatitude
    val driverLongitude: Double? get() = trip?.driverLongitude
    val allDroppedOff: Boolean get() = passengers.isNotEmpty() &&
            passengers.all { it.status is com.juanpablo0612.carpool.domain.trip.model.PickupStatus.DroppedOff }

    /**
     * Whether the driver may end the trip. Distinct from [allDroppedOff], which is false for an
     * empty passenger list — gating the button on that alone would strand a driver whose trip
     * nobody booked, with no way to close it out.
     */
    val canCompleteTrip: Boolean get() = passengers.isEmpty() || allDroppedOff

    /** A finished trip's thread is history: readable, not writable. */
    val isChatReadOnly: Boolean
        get() = trip?.status is com.juanpablo0612.carpool.domain.trip.model.TripStatus.Completed ||
                trip?.status is com.juanpablo0612.carpool.domain.trip.model.TripStatus.Cancelled
}
