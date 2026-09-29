package com.juanpablo0612.carpool.presentation.booking.driver.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.juanpablo0612.carpool.presentation.booking.driver.TripBookings
import com.juanpablo0612.carpool.presentation.ui.components.Pill
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.departureDayLabel
import com.juanpablo0612.carpool.presentation.ui.util.formatTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.booking_requests_free_seats
import enrutadoseia.composeapp.generated.resources.booking_requests_trip_full
import enrutadoseia.composeapp.generated.resources.chevron_right_24px
import enrutadoseia.composeapp.generated.resources.relative_day_at_time
import enrutadoseia.composeapp.generated.resources.route_from_to
import enrutadoseia.composeapp.generated.resources.trip_action_view_passengers
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Heads a trip's bookings in "Solicitudes": when it leaves, its route and its free seats, which
 * decide what the driver can still accept. Tapping it opens the trip's passengers.
 */
@Composable
internal fun TripBookingsHeader(
    group: TripBookings,
    nowMs: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(
                onClickLabel = stringResource(Res.string.trip_action_view_passengers),
                role = Role.Button,
                onClick = onClick,
            )
            .padding(vertical = Spacing.xs)
            .semantics(mergeDescendants = true) { heading() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(
                    Res.string.relative_day_at_time,
                    departureDayLabel(group.departureTime, nowMs).replaceFirstChar { it.uppercaseChar() },
                    formatTime(group.departureTime),
                ),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = stringResource(Res.string.route_from_to, group.originName, group.destinationName),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        group.freeSeats?.let { free ->
            if (free == 0) {
                Pill(
                    text = stringResource(Res.string.booking_requests_trip_full),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
            } else {
                Pill(
                    text = pluralStringResource(Res.plurals.booking_requests_free_seats, free, free),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
        Icon(
            imageVector = vectorResource(Res.drawable.chevron_right_24px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
