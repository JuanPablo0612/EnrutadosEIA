package com.juanpablo0612.carpool.domain.booking.model

data class Booking(
    val id: String = "",
    val tripId: String,
    val passengerId: String,
    val driverId: String,
    val passengerName: String,
    val passengerEmail: String,
    val status: BookingStatus,
    val createdAt: Long
)
