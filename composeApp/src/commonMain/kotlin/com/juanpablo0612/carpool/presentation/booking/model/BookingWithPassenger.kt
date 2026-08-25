package com.juanpablo0612.carpool.presentation.booking.model

import com.juanpablo0612.carpool.domain.booking.model.Booking

data class BookingWithPassenger(
    val booking: Booking,
    val passenger: PassengerSummary,
    val tripSummary: TripSummary,
)
