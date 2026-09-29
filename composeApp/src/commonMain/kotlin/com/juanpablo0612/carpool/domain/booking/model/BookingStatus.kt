package com.juanpablo0612.carpool.domain.booking.model

sealed class BookingStatus {
    data object Pending : BookingStatus()
    data object Confirmed : BookingStatus()
    data object Rejected : BookingStatus()
    data object Cancelled : BookingStatus()

    /** The request is closed: it will never become a seat, whatever happens to the trip. */
    val isTerminal: Boolean
        get() = this == Rejected || this == Cancelled
}
