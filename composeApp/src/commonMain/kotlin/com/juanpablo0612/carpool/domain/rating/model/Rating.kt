package com.juanpablo0612.carpool.domain.rating.model

data class Rating(
    val id: String,
    val tripId: String,
    val bookingId: String,
    val raterId: String,
    val rateeId: String,
    val stars: Int,
    val chips: List<RatingChip>,
    val comment: String?,
    val createdAt: Long
)
