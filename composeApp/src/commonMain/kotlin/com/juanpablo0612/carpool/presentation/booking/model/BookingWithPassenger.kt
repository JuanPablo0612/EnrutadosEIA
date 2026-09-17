package com.juanpablo0612.carpool.presentation.booking.model

import com.juanpablo0612.carpool.domain.booking.model.Booking

data class BookingWithPassenger(
    val booking: Booking,
    val passenger: PassengerSummary,
    val tripSummary: TripSummary,
)

fun Booking.toBookingWithPassenger() = BookingWithPassenger(
    booking = this,
    passenger = PassengerSummary(
        id = passengerId,
        name = passengerName,
        averageRating = null,
        tripsCompleted = 0,
        isEiaVerified = passengerEmail.endsWith("@eia.edu.co"),
    ),
    tripSummary = TripSummary(
        tripId = tripId,
        originName = originName,
        destinationName = destinationName,
        departureAt = departureTime,
    ),
)
