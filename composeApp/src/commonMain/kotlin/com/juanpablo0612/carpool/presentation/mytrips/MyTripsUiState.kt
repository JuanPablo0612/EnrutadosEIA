package com.juanpablo0612.carpool.presentation.mytrips

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_action_failed
import enrutadoseia.composeapp.generated.resources.error_trip_closed
import enrutadoseia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

/** A destructive or state-changing step waiting on the user's confirmation. */
sealed class MyTripsConfirmation {
    data class StartTrip(val tripId: String) : MyTripsConfirmation()
    data class CancelTrip(val tripId: String) : MyTripsConfirmation()
    data class CancelBooking(val booking: Booking) : MyTripsConfirmation() {
        val isPending: Boolean get() = booking.status == BookingStatus.Pending
    }
}

sealed class MyTripsError {
    data object LoadFailed : MyTripsError()
    data object ActionFailed : MyTripsError()

    /** The seat could not be withdrawn because the trip has already left. */
    data object TripClosed : MyTripsError()

    fun asStringResource(): StringResource = when (this) {
        LoadFailed -> Res.string.error_unknown
        ActionFailed -> Res.string.error_action_failed
        TripClosed -> Res.string.error_trip_closed
    }
}

data class MyTripsUiState(
    val isLoading: Boolean = true,
    val items: List<MyTripItem> = emptyList(),
    val segment: MyTripsSegment = MyTripsSegment.Upcoming,
    val filter: MyTripsFilter = MyTripsFilter.All,
    /** Seat requests waiting on the user as a driver, surfaced as a banner. */
    val pendingRequestCount: Int = 0,
    val confirmation: MyTripsConfirmation? = null,
    /** An item whose start, cancel or withdrawal is in flight; its actions are disabled meanwhile. */
    val busyKey: String? = null,
    val error: MyTripsError? = null,
)
