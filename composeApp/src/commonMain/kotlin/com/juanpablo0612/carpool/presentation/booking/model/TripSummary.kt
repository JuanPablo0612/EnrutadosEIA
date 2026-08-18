package com.juanpablo0612.carpool.presentation.booking.model

data class TripSummary(
    val tripId: String,
    val originName: String,
    val destinationName: String,
    val departureAt: Long,
)
