package com.juanpablo0612.carpool.domain.trip.model

sealed class TripStatus {
    data object Active : TripStatus()
    data object Cancelled : TripStatus()
    data object Completed : TripStatus()
}
