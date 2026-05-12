package com.juanpablo0612.carpool.domain.booking.model

data class TripSummary(
    val tripId: String,
    val originName: String,
    val destinationName: String,
    val departureAt: Long,
)
