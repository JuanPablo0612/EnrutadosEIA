package com.juanpablo0612.carpool.domain.booking.model

data class PassengerSummary(
    val id: String,
    val name: String,
    val averageRating: Double?,
    val tripsCompleted: Int,
    val isEiaVerified: Boolean,
)
