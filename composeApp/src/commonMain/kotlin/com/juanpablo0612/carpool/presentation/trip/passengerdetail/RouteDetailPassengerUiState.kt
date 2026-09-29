package com.juanpablo0612.carpool.presentation.trip.passengerdetail

import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.presentation.booking.BookingError

data class RouteDetailPassengerUiState(
    val isLoading: Boolean = true,
    /** The trip couldn't be read, or no longer exists. */
    val loadFailed: Boolean = false,
    val trip: Trip? = null,
    /** The driver's live profile, for the rating; name and photo come from the trip's snapshot. */
    val driver: PublicProfile? = null,
    val meetingStop: TripMeetingStop? = null,
    val alreadyRequested: Boolean = false,
    /** True when the signed-in user is this trip's driver — hides the booking bar entirely. */
    val isOwner: Boolean = false,
    val isBooking: Boolean = false,
    val showConfirmSheet: Boolean = false,
    val passengerMessage: String = "",
    /** True right after a booking request succeeds, briefly, before navigating to Mis viajes. */
    val bookingRequestSent: Boolean = false,
    val error: BookingError? = null
) {
    /** Seats come straight from the trip document, so no booking needs to be read. */
    val availableSeats: Int
        get() = trip?.let { (it.seatCount - it.confirmedSeats).coerceAtLeast(0) } ?: 0
}
