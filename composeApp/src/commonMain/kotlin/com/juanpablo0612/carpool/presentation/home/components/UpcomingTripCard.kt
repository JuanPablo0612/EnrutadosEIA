package com.juanpablo0612.carpool.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.presentation.home.UpcomingTrip
import com.juanpablo0612.carpool.presentation.ui.components.BookingStatusBadge
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.Pill
import com.juanpablo0612.carpool.presentation.ui.components.RouteLineRow
import com.juanpablo0612.carpool.presentation.ui.components.RouteTimeline
import com.juanpablo0612.carpool.presentation.ui.components.SecondaryButton
import com.juanpablo0612.carpool.presentation.ui.components.TimelineStop
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.departureDayLabel
import com.juanpablo0612.carpool.presentation.ui.util.formatTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.chevron_right_24px
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.home_departs_in
import enrutadoseia.composeapp.generated.resources.home_role_driving
import enrutadoseia.composeapp.generated.resources.home_role_riding
import enrutadoseia.composeapp.generated.resources.trip_action_view_passengers
import enrutadoseia.composeapp.generated.resources.trip_seats_occupied
import enrutadoseia.composeapp.generated.resources.trip_waypoint_count
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/** Under an hour away, the card counts down in minutes instead of naming the day. */
private const val COUNTDOWN_WINDOW_MS = 60 * 60 * 1000L

/**
 * The hero card for the user's next trip: role, when it leaves, the big departure time and the
 * route. A trip the user drives adds how full it is and a shortcut to its passengers.
 */
@Composable
fun UpcomingTripCard(
    upcoming: UpcomingTrip,
    now: Long,
    onOpen: () -> Unit,
    onOpenPassengers: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CarpoolListCard(
        modifier = modifier,
        onClick = onOpen,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RolePill(upcoming)
                Spacer(modifier = Modifier.weight(1f))
                DepartureCountdown(departureTime = upcoming.departureTime, now = now)
            }
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Text(text = formatTime(upcoming.departureTime), style = MaterialTheme.typography.displaySmall)
                Text(
                    text = departureDayLabel(upcoming.departureTime, now),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            RouteTimeline(stops = upcoming.timelineStops())
            when (upcoming) {
                is UpcomingTrip.Driving -> {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    SeatOccupancy(taken = upcoming.trip.confirmedSeats, total = upcoming.trip.seatCount)
                    SecondaryButton(
                        text = stringResource(Res.string.trip_action_view_passengers),
                        onClick = onOpenPassengers,
                    )
                }
                is UpcomingTrip.Riding -> BookingStatusBadge(status = BookingStatus.Confirmed)
            }
        }
    }
}

/** A compact row for the trip after [UpcomingTripCard]'s, on the other side of the car. */
@Composable
fun LaterTripRow(
    upcoming: UpcomingTrip,
    now: Long,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (origin, destination) = upcoming.endpoints()
    CarpoolListCard(modifier = modifier, onClick = onOpen) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            Column(modifier = Modifier.width(88.dp)) {
                Text(text = formatTime(upcoming.departureTime), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = departureDayLabel(upcoming.departureTime, now),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                RouteLineRow(
                    origin = origin,
                    destination = destination,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = stringResource(upcoming.roleLabel()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = vectorResource(Res.drawable.chevron_right_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RolePill(upcoming: UpcomingTrip) {
    when (upcoming) {
        is UpcomingTrip.Driving -> Pill(
            text = stringResource(Res.string.home_role_driving),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            icon = vectorResource(Res.drawable.directions_car_24px),
        )
        is UpcomingTrip.Riding -> Pill(
            text = stringResource(Res.string.home_role_riding),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DepartureCountdown(departureTime: Long, now: Long) {
    val remaining = departureTime - now
    if (remaining in 0 until COUNTDOWN_WINDOW_MS) {
        Text(
            text = stringResource(Res.string.home_departs_in, (remaining / 60_000).toInt()),
            style = MaterialTheme.typography.titleSmall,
            color = LocalExtendedColors.current.warning,
        )
    }
}

/**
 * How full the trip is, as segments plus text. The text carries the meaning; the bar is
 * decorative, so it is hidden from screen readers.
 */
@Composable
private fun SeatOccupancy(taken: Int, total: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clearAndSetSemantics {},
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            repeat(total.coerceAtLeast(1)) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .background(
                            color = if (index < taken) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            },
                            shape = CircleShape,
                        )
                )
            }
        }
        Text(
            text = stringResource(Res.string.trip_seats_occupied, taken, total),
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
private fun UpcomingTrip.timelineStops(): List<TimelineStop> = when (this) {
    is UpcomingTrip.Driving -> listOf(
        TimelineStop(title = trip.origin.name),
        TimelineStop(
            title = trip.destination.name,
            caption = trip.waypoints.size.takeIf { it > 0 }?.let {
                pluralStringResource(Res.plurals.trip_waypoint_count, it, it)
            },
        ),
    )
    is UpcomingTrip.Riding -> listOf(
        TimelineStop(title = booking.originName),
        TimelineStop(title = booking.destinationName),
    )
}

private fun UpcomingTrip.endpoints(): Pair<String, String> = when (this) {
    is UpcomingTrip.Driving -> trip.origin.name to trip.destination.name
    is UpcomingTrip.Riding -> booking.originName to booking.destinationName
}

private fun UpcomingTrip.roleLabel() = when (this) {
    is UpcomingTrip.Driving -> Res.string.home_role_driving
    is UpcomingTrip.Riding -> Res.string.home_role_riding
}
