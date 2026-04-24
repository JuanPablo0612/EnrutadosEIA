package com.juanpablo0612.carpool.domain.booking.model

sealed class BookingStatus {
    data object Pending : BookingStatus()
    data object Confirmed : BookingStatus()
    data object Rejected : BookingStatus()
    data object Cancelled : BookingStatus()
}
