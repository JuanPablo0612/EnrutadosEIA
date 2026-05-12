package com.juanpablo0612.carpool.domain.booking.model

data class Booking(
    val id: String = "",
    val tripId: String,
    val passengerId: String,
    val driverId: String,
    val passengerName: String,
    val passengerEmail: String,
    val originName: String,
    val destinationName: String,
    val departureTime: Long,
    val status: BookingStatus,
    val createdAt: Long,
    val passengerMessage: String? = null,
    val rejectReason: RejectReason? = null,
    val rejectComment: String? = null,
)
