package com.juanpablo0612.carpool.presentation.bookings.passenger.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.presentation.ui.components.BookingStatusBadge
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import enrutadoseia.composeapp.generated.resources.booking_action_rate
import enrutadoseia.composeapp.generated.resources.booking_status_subtitle_cancelled
import enrutadoseia.composeapp.generated.resources.booking_status_subtitle_confirmed
import enrutadoseia.composeapp.generated.resources.booking_status_subtitle_pending
import enrutadoseia.composeapp.generated.resources.booking_status_subtitle_rejected
import enrutadoseia.composeapp.generated.resources.cancel_booking_button
import enrutadoseia.composeapp.generated.resources.trip_action_track
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

// TODO: show vehicle info once vehicleId is stored in Booking

@Composable
fun EnrichedBookingCard(
    booking: Booking,
    onCancelClick: (String) -> Unit,
    onTrackTrip: ((tripId: String) -> Unit)? = null,
    onRateBooking: ((bookingId: String, tripId: String, rateeId: String, rateeName: String) -> Unit)? = null,
    nowMs: Long = kotlin.time.Clock.System.now().toEpochMilliseconds(),
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = formatDepartureTime(booking.departureTime),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                BookingStatusBadge(status = booking.status)
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = booking.originName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                Icon(
                    imageVector = vectorResource(Res.drawable.arrow_forward_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = Spacing.xs).size(16.dp)
                )
                Text(
                    text = booking.destinationName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                    maxLines = 1
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

private fun formatDepartureTime(epochMs: Long): String {
    val local = Instant.fromEpochMilliseconds(epochMs)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = local.hour.toString().padStart(2, '0')
    val minute = local.minute.toString().padStart(2, '0')
    @Suppress("DEPRECATION")
    val day = local.dayOfMonth.toString().padStart(2, '0')
    @Suppress("DEPRECATION")
    val month = local.monthNumber.toString().padStart(2, '0')
    return "$day/$month · $hour:$minute"
}
