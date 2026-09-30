package com.juanpablo0612.carpool.presentation.home

import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.trip.model.Trip

/** One upcoming trip on Inicio, whichever side of it the user is on. */
sealed class UpcomingTrip {
    abstract val tripId: String
    abstract val departureTime: Long

    /** A trip the user drives. */
    data class Driving(val trip: Trip) : UpcomingTrip() {
        override val tripId: String get() = trip.id
        override val departureTime: Long get() = trip.departureTime
    }

    /** A confirmed seat the user has in someone else's trip. */
    data class Riding(val booking: Booking) : UpcomingTrip() {
        override val tripId: String get() = booking.tripId
        override val departureTime: Long get() = booking.departureTime
    }
}

data class HomeUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    /** The soonest upcoming trip, driving or riding. */
    val nextUp: UpcomingTrip? = null,
    /** The soonest trip on the other side, shown compactly below [nextUp]. */
    val later: UpcomingTrip? = null,
    /** Seat requests waiting on the user as a driver. */
    val pendingRequestCount: Int = 0,
    val hasVehicles: Boolean = false,
    /** Whether the user has ever asked for a seat, whatever became of it. */
    val hasBookedBefore: Boolean = false,
    val vehicleSuggestionDismissed: Boolean = false,
    val error: HomeError? = null,
) {
    val welcome: HomeWelcome
        get() = HomeWelcome.of(
            hasBookedBefore = hasBookedBefore,
            hasVehicles = hasVehicles,
            hasUpcomingTrip = nextUp != null,
            vehicleSuggestionDismissed = vehicleSuggestionDismissed,
        )

    /** Publishing needs a vehicle; without one the button would lead to a dead end. */
    val canPublishTrip: Boolean
        get() = hasVehicles
}
