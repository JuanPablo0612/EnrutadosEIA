package com.juanpablo0612.carpool.presentation.bookings.driver.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.booking.model.BookingWithPassenger
import com.juanpablo0612.carpool.domain.booking.model.PassengerSummary
import com.juanpablo0612.carpool.presentation.home.relativeTime
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import enrutadoseia.composeapp.generated.resources.booking_request_accept_button
import enrutadoseia.composeapp.generated.resources.booking_request_eia_verified
import enrutadoseia.composeapp.generated.resources.booking_request_trip_context
import enrutadoseia.composeapp.generated.resources.booking_request_trips_count
import enrutadoseia.composeapp.generated.resources.booking_request_view_profile
import enrutadoseia.composeapp.generated.resources.reject_button
import enrutadoseia.composeapp.generated.resources.relative_days_ago
import enrutadoseia.composeapp.generated.resources.relative_hours_ago
import enrutadoseia.composeapp.generated.resources.relative_just_now
import enrutadoseia.composeapp.generated.resources.relative_minutes_ago
import kotlin.time.Clock
import kotlin.time.Instant
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BookingRequestCard(
    item: BookingWithPassenger,
    processingIds: Set<String>,
    onAccept: (String, String) -> Unit,
    onReject: (String) -> Unit,
    onViewProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val booking = item.booking
    val passenger = item.passenger
    val isProcessing = booking.id in processingIds
    val firstName = remember(passenger.name) { passenger.name.split(" ").firstOrNull() ?: passenger.name }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
        ) {
            // Header row: avatar + name/reputation + timestamp
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                UserAvatar(name = passenger.name, size = 48.dp)
                Spacer(modifier = Modifier.width(Spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = passenger.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    ReputationLine(passenger = passenger)
                }
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = relativeCreatedAt(booking.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Trip context
            Text(
                text = stringResource(
                    Res.string.booking_request_trip_context,
                    relativeTime(booking.departureTime),
                ),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = booking.originName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )
                Icon(
                    imageVector = vectorResource(Res.drawable.arrow_forward_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = Spacing.xs)
                        .size(14.dp),
                )
                Text(
                    text = booking.destinationName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )
            }

            // Optional passenger message
            if (!booking.passengerMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "💬 \"${booking.passengerMessage}\"",
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(Spacing.sm),
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Button(
                    onClick = { onAccept(booking.id, booking.tripId) },
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(Res.string.booking_request_accept_button))
                }
                OutlinedButton(
                    onClick = { onReject(booking.id) },
                    enabled = !isProcessing,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error),
                    ),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(Res.string.reject_button))
                }
            }

            // View profile link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = { onViewProfile(booking.passengerId) }) {
                    Text(
                        text = stringResource(Res.string.booking_request_view_profile, firstName),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReputationLine(passenger: PassengerSummary) {
    val parts = buildList {
        if (passenger.averageRating != null) add("⭐ ${"%.1f".format(passenger.averageRating)}")
        if (passenger.tripsCompleted > 0) add(
            stringResource(Res.string.booking_request_trips_count, passenger.tripsCompleted)
        )
        if (passenger.isEiaVerified) add(stringResource(Res.string.booking_request_eia_verified))
    }
    if (parts.isNotEmpty()) {
        Text(
            text = parts.joinToString(" · "),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun relativeCreatedAt(createdAtMs: Long): String {
    val now = Clock.System.now().toEpochMilliseconds()
    val diffMs = now - createdAtMs
    val diffMin = diffMs / 60_000
    val diffHour = diffMs / 3_600_000
    val diffDay = diffMs / 86_400_000
    return when {
        diffMin < 1 -> stringResource(Res.string.relative_just_now)
        diffMin < 60 -> stringResource(Res.string.relative_minutes_ago, diffMin)
        diffHour < 24 -> stringResource(Res.string.relative_hours_ago, diffHour)
        else -> stringResource(Res.string.relative_days_ago, diffDay)
    }
}
