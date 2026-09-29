package com.juanpablo0612.carpool.presentation.trip.passengerdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.rating.model.RatingSummary
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.presentation.trip.description
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.TripMeetingStop
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.RatingBadge
import com.juanpablo0612.carpool.presentation.ui.components.RouteTimeline
import com.juanpablo0612.carpool.presentation.ui.components.TimelineStop
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.contributionLabel
import com.juanpablo0612.carpool.presentation.ui.util.departureDayLabel
import com.juanpablo0612.carpool.presentation.ui.util.formatTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.chevron_right_24px
import enrutadoseia.composeapp.generated.resources.trip_detail_arrival
import enrutadoseia.composeapp.generated.resources.trip_detail_contribution
import enrutadoseia.composeapp.generated.resources.trip_detail_departs_at
import enrutadoseia.composeapp.generated.resources.trip_detail_departure_caption
import enrutadoseia.composeapp.generated.resources.trip_detail_driver_says
import enrutadoseia.composeapp.generated.resources.trip_detail_free_seats
import enrutadoseia.composeapp.generated.resources.trip_detail_route
import enrutadoseia.composeapp.generated.resources.trip_detail_seats_value
import enrutadoseia.composeapp.generated.resources.trip_detail_you_get_off
import enrutadoseia.composeapp.generated.resources.trip_detail_you_get_on
import enrutadoseia.composeapp.generated.resources.trip_driver_placeholder
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/** The day as an overline and the departure time as the screen's headline. */
@Composable
internal fun TripDetailHeader(trip: Trip, now: Long, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(
            text = departureDayLabel(trip.departureTime, now),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(Res.string.trip_detail_departs_at, formatTime(trip.departureTime)),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() },
        )
    }
}

/** Contribution and free seats, the two numbers a passenger decides on. */
@Composable
internal fun TripStats(trip: Trip, availableSeats: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        StatTile(
            label = stringResource(Res.string.trip_detail_contribution),
            value = contributionLabel(trip.contributionPerPassenger),
            modifier = Modifier.weight(1f),
        )
        StatTile(
            label = stringResource(Res.string.trip_detail_free_seats),
            value = stringResource(Res.string.trip_detail_seats_value, availableSeats, trip.seatCount),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    CarpoolListCard(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleLarge)
    }
}

/**
 * The trip's stops in order, with the passenger's own stop marked when they came from a search.
 * The departure time rides on the origin; the other stops have no times of their own.
 */
@Composable
internal fun TripRouteCard(trip: Trip, meetingStop: TripMeetingStop?, modifier: Modifier = Modifier) {
    val path = listOf(trip.origin) + trip.waypoints + trip.destination
    val meetingCaption = meetingStop?.let {
        stringResource(if (it.isDropoff) Res.string.trip_detail_you_get_off else Res.string.trip_detail_you_get_on)
    }
    val departureCaption = stringResource(Res.string.trip_detail_departure_caption, formatTime(trip.departureTime))
    val arrivalCaption = stringResource(Res.string.trip_detail_arrival)
    val stops = path.mapIndexed { index, place ->
        val isMeeting = meetingStop?.pathIndex == index
        TimelineStop(
            title = place.name,
            caption = when {
                isMeeting -> meetingCaption
                index == 0 -> departureCaption
                index == path.lastIndex -> arrivalCaption
                else -> null
            },
            isHighlighted = isMeeting,
        )
    }
    CarpoolListCard(modifier = modifier) {
        Text(
            text = stringResource(Res.string.trip_detail_route),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        RouteTimeline(stops = stops, modifier = Modifier.fillMaxWidth().padding(top = Spacing.md))
    }
}

/**
 * Who drives, in what. Name, photo and car come with the trip; the rating comes from the profile
 * read and appears once it arrives. Tapping opens the driver's profile.
 */
@Composable
internal fun DriverCard(
    trip: Trip,
    rating: RatingSummary?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val name = trip.driver.name.ifBlank { stringResource(Res.string.trip_driver_placeholder) }
    CarpoolListCard(modifier = modifier, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            UserAvatar(name = name, photoUrl = trip.driver.photoUrl, size = 48.dp)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                rating?.let { RatingBadge(rating = it, showCount = true, textStyle = MaterialTheme.typography.bodySmall) }
                trip.vehicle.description()?.let {
                    Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(
                imageVector = vectorResource(Res.drawable.chevron_right_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** The driver's note to passengers, attributed by first name. */
@Composable
internal fun DriverMessageCard(driverName: String, message: String, modifier: Modifier = Modifier) {
    CarpoolListCard(modifier = modifier) {
        Text(
            text = stringResource(Res.string.trip_detail_driver_says, driverName.substringBefore(' ')),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = message, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = Spacing.xs))
    }
}
