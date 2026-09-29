package com.juanpablo0612.carpool.presentation.mytrips

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_action_failed
import enrutadoseia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

/** A destructive or state-changing step waiting on the user's confirmation. */
sealed class MyTripsConfirmation {
    data class StartTrip(val tripId: String) : MyTripsConfirmation()
    data class CancelTrip(val tripId: String) : MyTripsConfirmation()
    data class CancelBooking(val bookingId: String, val isPending: Boolean) : MyTripsConfirmation()
}

sealed class MyTripsError {
    data object LoadFailed : MyTripsError()
    data object ActionFailed : MyTripsError()

    fun asStringResource(): StringResource = when (this) {
        LoadFailed -> Res.string.error_unknown
        ActionFailed -> Res.string.error_action_failed
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
