package com.juanpablo0612.carpool.domain.booking.model

data class BookingWithPassenger(
    val booking: Booking,
    val passenger: PassengerSummary,
    val tripSummary: TripSummary,
)
