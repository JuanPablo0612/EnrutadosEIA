package com.juanpablo0612.carpool.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.presentation.ui.components.HighlightCard
import com.juanpablo0612.carpool.presentation.ui.components.TripStatusBadge
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.home_next_trip_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun NextTripCard(
    trip: Trip,
    now: Long,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HighlightCard(
        title = stringResource(Res.string.home_next_trip_title),
        statusBadge = { TripStatusBadge(status = trip.status) },
        timeText = relativeTime(trip.departureTime, now),
        origin = trip.origin.name,
        destination = trip.destination.name,
        onClick = onTap,
        modifier = modifier,
    )
}
