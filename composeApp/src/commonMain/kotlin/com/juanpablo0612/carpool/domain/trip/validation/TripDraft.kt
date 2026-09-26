package com.juanpablo0612.carpool.domain.trip.validation

import com.juanpablo0612.carpool.domain.place.model.Place
import kotlin.time.Instant

/** A trip as the driver has filled it in, before it is published. */
data class TripDraft(
    val origin: Place?,
    val destination: Place?,
    val departure: Instant,
    /** Seats the selected vehicle can offer, or null when no vehicle is selected. */
    val vehicleCapacity: Int?,
    val seatCount: Int,
    val contributionPerPassenger: Int?,
    val message: String,
    /** Set when the driver also wants to save these stops as a frequent route. */
    val routeNameToSave: String? = null,
)
