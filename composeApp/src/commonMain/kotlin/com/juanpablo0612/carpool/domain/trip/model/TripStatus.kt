package com.juanpablo0612.carpool.domain.trip.model

sealed class TripStatus {
    data object Active : TripStatus()
    data object InProgress : TripStatus()
    data object Completed : TripStatus()
    data object Cancelled : TripStatus()
}
