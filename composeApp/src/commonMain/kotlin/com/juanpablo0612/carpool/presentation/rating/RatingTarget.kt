package com.juanpablo0612.carpool.presentation.rating

/**
 * Who is being rated, for which booking, and the trip it was. Whoever opens the rating screen
 * already holds all of it (a booking carries the other party's name and photo and the trip's
 * route), so the screen shows it without reading anything.
 */
data class RatingTarget(
    val bookingId: String,
    val tripId: String,
    val rateeId: String,
    val rateeName: String,
    val rateePhotoUrl: String? = null,
    /** Whether the person being rated drove: it picks the highlights offered. */
    val rateeIsDriver: Boolean,
    /** The trip's departure, or null when the opener doesn't know it. */
    val departureTime: Long? = null,
    val originName: String = "",
    val destinationName: String = "",
)
