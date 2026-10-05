package com.juanpablo0612.carpool.presentation.trip.edit

import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.place.stops.SelectionTarget
import com.juanpablo0612.carpool.presentation.place.stops.StopsDraft
import com.juanpablo0612.carpool.presentation.trip.TripError

data class EditTripUiState(
    val isLoading: Boolean = true,
    /** The trip as published; the draft below is compared against it. */
    val trip: Trip? = null,
    /** The trip's car, for the seat cap; null if it couldn't be read. */
    val vehicle: Vehicle? = null,
    val seatCount: Int = 1,
    val stops: StopsDraft = StopsDraft(),
    /**
     * Stops, by name, where an open booking meets the trip: they are locked from the start. Empty
     * if the bookings couldn't be read; saving then still refuses to drop a stop in use.
     */
    val stopUsers: Map<String, StopUsers> = emptyMap(),
    val selectionTarget: SelectionTarget? = null,
    val isSaving: Boolean = false,
    val showDiscardConfirm: Boolean = false,
    /** Why the trip couldn't be loaded; the screen shows a retry instead of the form. */
    val loadError: TripError? = null,
    val error: TripError? = null,
) {
    val isDirty: Boolean
        get() = trip != null && (seatCount != trip.seatCount || stops.waypoints != trip.waypoints)

    /** Whether the waypoint at [index] was chosen by a passenger and so can't change. */
    fun isWaypointLocked(index: Int): Boolean = stops.waypoints.getOrNull(index)?.name in stopUsers

    /** Seats can't drop below those already given to passengers. */
    val minSeats: Int
        get() = maxOf(1, trip?.confirmedSeats ?: 0)
}
