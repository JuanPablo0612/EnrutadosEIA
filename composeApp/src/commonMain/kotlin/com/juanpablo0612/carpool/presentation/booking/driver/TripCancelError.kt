package com.juanpablo0612.carpool.presentation.booking.driver

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_trip_cancel_failed
import org.jetbrains.compose.resources.StringResource

/** Why cancelling a trip from its passengers screen failed, shown inside the confirmation dialog. */
sealed class TripCancelError {
    data object Failed : TripCancelError()

    fun asStringResource(): StringResource = when (this) {
        Failed -> Res.string.error_trip_cancel_failed
    }
}
