package com.juanpablo0612.carpool.presentation.trip.tracking

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_action_failed
import org.jetbrains.compose.resources.StringResource

/**
 * Self-contained presentation error for trip-tracking mutation failures (mark picked up, mark
 * dropped off, complete trip). Follows the AddPlaceError / HomeError style described in
 * CLAUDE.md: a screen-local error type that never touches the domain layer, since these failures
 * surface directly from a use case [Result] without needing a richer domain taxonomy.
 */
sealed class TripTrackingError {
    data object ActionFailed : TripTrackingError()

    fun asStringResource(): StringResource = when (this) {
        ActionFailed -> Res.string.error_action_failed
    }
}
