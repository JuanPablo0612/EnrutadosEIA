package com.juanpablo0612.carpool.presentation.trip.tracking

import com.juanpablo0612.carpool.domain.trip.model.PickupStatus

data class PassengerWithStatus(
    val passengerId: String,
    val passengerName: String,
    val bookingId: String,
    val status: PickupStatus
)
