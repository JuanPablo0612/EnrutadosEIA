package com.juanpablo0612.carpool.presentation.booking.driver.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import com.juanpablo0612.carpool.presentation.booking.driver.decision.BookingDecisionAction
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.SecondaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.booking_request_accept_button
import enrutadoseia.composeapp.generated.resources.booking_request_last_seat
import enrutadoseia.composeapp.generated.resources.booking_request_message_quote
import enrutadoseia.composeapp.generated.resources.booking_request_trip_full
import enrutadoseia.composeapp.generated.resources.error_24px
import enrutadoseia.composeapp.generated.resources.info_24px
import enrutadoseia.composeapp.generated.resources.reject_button
import enrutadoseia.composeapp.generated.resources.relative_days_ago
import enrutadoseia.composeapp.generated.resources.relative_hours_ago
import enrutadoseia.composeapp.generated.resources.relative_just_now
import enrutadoseia.composeapp.generated.resources.relative_minutes_ago
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * A seat request waiting on the driver: who asks, where they get on or off, what they wrote and
 * the answer. What the trip's seats allow is spelled out before the buttons, so accepting never
 * fails by surprise: a full trip can only reject (with "trip full" already chosen as the reason).
 */
@Composable
internal fun BookingRequestCard(
    booking: Booking,
    isTripFull: Boolean,
    isLastSeatContested: Boolean,
    isBusy: Boolean,
    nowMs: Long,
    onDecision: (BookingDecisionAction) -> Unit,
    onViewProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CarpoolListCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            PassengerHeader(booking = booking, onViewProfile = onViewProfile) {
                Text(
                    text = requestedAgo(booking.createdAt, nowMs),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            booking.meetingStop?.let { MeetingStopLine(stop = it) }
            booking.passengerMessage?.takeIf { it.isNotBlank() }?.let { message ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(Res.string.booking_request_message_quote, message),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                    )
                }
            }
            when {
                isTripFull -> SeatNotice(
                    icon = vectorResource(Res.drawable.error_24px),
                    text = stringResource(Res.string.booking_request_trip_full),
                    color = MaterialTheme.colorScheme.error,
                )
                isLastSeatContested -> SeatNotice(
                    icon = vectorResource(Res.drawable.info_24px),
                    text = stringResource(Res.string.booking_request_last_seat),
                    color = LocalExtendedColors.current.onWarningContainer,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                SecondaryButton(
                    text = stringResource(Res.string.reject_button),
                    onClick = {
                        val suggested = if (isTripFull) RejectReason.TripFull else null
                        onDecision(BookingDecisionAction.Reject(booking, suggested))
                    },
                    enabled = !isBusy,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                PrimaryButton(
                    text = stringResource(Res.string.booking_request_accept_button),
                    onClick = { onDecision(BookingDecisionAction.Accept(booking)) },
                    enabled = !isTripFull,
                    isLoading = isBusy,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SeatNotice(icon: ImageVector, text: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalAlignment = Alignment.Top) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

@Composable
private fun requestedAgo(createdAtMs: Long, nowMs: Long): String {
    val minutes = (nowMs - createdAtMs).coerceAtLeast(0) / 60_000
    return when {
        minutes < 1 -> stringResource(Res.string.relative_just_now)
        minutes < 60 -> stringResource(Res.string.relative_minutes_ago, minutes)
        minutes < 24 * 60 -> stringResource(Res.string.relative_hours_ago, minutes / 60)
        else -> stringResource(Res.string.relative_days_ago, minutes / (24 * 60))
    }
}
