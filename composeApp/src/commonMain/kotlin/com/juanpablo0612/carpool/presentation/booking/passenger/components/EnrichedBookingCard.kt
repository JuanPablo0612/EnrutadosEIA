package com.juanpablo0612.carpool.presentation.booking.passenger.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.presentation.ui.components.BookingStatusBadge
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.RouteLineRow
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.formatDayMonthTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.booking_action_rate
import enrutadoseia.composeapp.generated.resources.booking_status_subtitle_cancelled
import enrutadoseia.composeapp.generated.resources.booking_status_subtitle_confirmed
import enrutadoseia.composeapp.generated.resources.booking_status_subtitle_pending
import enrutadoseia.composeapp.generated.resources.booking_status_subtitle_rejected
import enrutadoseia.composeapp.generated.resources.booking_action_message_driver
import enrutadoseia.composeapp.generated.resources.cancel_booking_button
import enrutadoseia.composeapp.generated.resources.trip_action_track
import org.jetbrains.compose.resources.stringResource

@Composable
fun EnrichedBookingCard(
    booking: Booking,
    // Booking carries no driver-name/vehicle field of its own — both are resolved by the caller
    // (driverName via a profile lookup, vehicleSummary via the trip's vehicleId) and passed in,
    // rather than making this card fetch anything itself.
    driverName: String? = null,
    vehicleSummary: String? = null,
    // Nullable like its siblings: the "Past" tab has nothing to cancel, and null hides the
    // Cancel button instead of rendering one that does nothing.
    onCancelClick: ((String) -> Unit)? = null,
    onTrackTrip: ((tripId: String) -> Unit)? = null,
    onRateBooking: ((bookingId: String, tripId: String, rateeId: String, rateeName: String) -> Unit)? = null,
    onMessageDriver: (() -> Unit)? = null,
    // Passed in rather than defaulted to `now`: a default read during composition makes this
    // composable non-idempotent and re-reads the clock on every recomposition.
    nowMs: Long,
    modifier: Modifier = Modifier
) {
    CarpoolListCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = formatDayMonthTime(booking.departureTime),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            BookingStatusBadge(status = booking.status)
        }

        Spacer(modifier = Modifier.height(Spacing.xs))

        RouteLineRow(origin = booking.originName, destination = booking.destinationName)

        if (driverName != null || vehicleSummary != null) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = listOfNotNull(driverName, vehicleSummary).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val subtitle = statusSubtitle(booking.status)
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val isConfirmed = booking.status is BookingStatus.Confirmed
        val isPast = booking.departureTime <= nowMs

        if (isConfirmed || booking.status is BookingStatus.Pending) {
            Spacer(modifier = Modifier.height(Spacing.md))
            if (isConfirmed && onTrackTrip != null) {
                Button(
                    onClick = { onTrackTrip(booking.tripId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(Res.string.trip_action_track))
                }
                Spacer(modifier = Modifier.height(Spacing.xs))
            }
            if (isConfirmed && isPast && onRateBooking != null) {
                Button(
                    onClick = { onRateBooking(booking.id, booking.tripId, booking.driverId, "") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(Res.string.booking_action_rate))
                }
                Spacer(modifier = Modifier.height(Spacing.xs))
            }
            if (isConfirmed && !isPast && onMessageDriver != null) {
                OutlinedButton(
                    onClick = onMessageDriver,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(Res.string.booking_action_message_driver))
                }
                Spacer(modifier = Modifier.height(Spacing.xs))
            }
            if (onCancelClick != null) {
                OutlinedButton(
                    onClick = { onCancelClick(booking.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(Res.string.cancel_booking_button))
                }
            }
        }
    }
}

@Composable
private fun statusSubtitle(status: BookingStatus): String? = when (status) {
    is BookingStatus.Pending -> stringResource(Res.string.booking_status_subtitle_pending)
    is BookingStatus.Confirmed -> stringResource(Res.string.booking_status_subtitle_confirmed)
    is BookingStatus.Rejected -> stringResource(Res.string.booking_status_subtitle_rejected)
    is BookingStatus.Cancelled -> stringResource(Res.string.booking_status_subtitle_cancelled)
}

