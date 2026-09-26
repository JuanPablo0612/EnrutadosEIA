package com.juanpablo0612.carpool.presentation.route.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.MatchedStop
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.presentation.route.search.TripResult
import com.juanpablo0612.carpool.presentation.route.search.formatDistance
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.DriverRatingBadge
import com.juanpablo0612.carpool.presentation.ui.components.RouteLineRow
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.util.formatDayMonthTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.location_on_24px
import enrutadoseia.composeapp.generated.resources.search_result_dropoff_distance
import enrutadoseia.composeapp.generated.resources.search_result_pickup_distance
import enrutadoseia.composeapp.generated.resources.search_result_pickup_here
import enrutadoseia.composeapp.generated.resources.trip_available_seats
import enrutadoseia.composeapp.generated.resources.trip_contribution_free
import enrutadoseia.composeapp.generated.resources.trip_driver_placeholder
import enrutadoseia.composeapp.generated.resources.trip_result_view_detail
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun TripResultCard(
    result: TripResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CarpoolListCard(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatDayMonthTime(result.trip.departureTime),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
            SeatsChip(availableSeats = result.availableSeats)
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        RouteLineRow(
            origin = result.trip.origin.name,
            destination = result.trip.destination.name,
            iconTint = MaterialTheme.colorScheme.primary
        )

        PickupDropoffInfo(pickup = result.pickup, dropoff = result.dropoff)

        Spacer(modifier = Modifier.height(Spacing.sm))

        val driverName = result.driver?.name?.takeIf { it.isNotBlank() }
            ?: stringResource(Res.string.trip_driver_placeholder)
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(name = driverName, photoUrl = result.driver?.photoUrl, size = 28.dp) // avatar-intrinsic size
            Spacer(modifier = Modifier.size(Spacing.sm))
            Text(
                text = driverName,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
            )
            result.driverAverageRating?.let { rating ->
                Spacer(modifier = Modifier.size(Spacing.sm))
                DriverRatingBadge(averageRating = rating)
            }
        }

        result.vehicle?.let { vehicle ->
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "${vehicle.brand} ${vehicle.model} · ${vehicle.color}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xs))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val contributionLabel = result.formattedContribution?.let { "$$it" }
                ?: stringResource(Res.string.trip_contribution_free)
            Text(
                text = contributionLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onClick) {
                Text(
                    stringResource(Res.string.trip_result_view_detail),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun SeatsChip(availableSeats: Int) {
    val extColors = LocalExtendedColors.current
    val (bg, fg) = if (availableSeats <= 1) {
        extColors.warning to extColors.onWarning
    } else {
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = bg
    ) {
        Text(
            text = stringResource(Res.string.trip_available_seats, availableSeats),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = fg,
            // vertical inset is a fine visual tweak below the 4dp scale step, kept as a literal
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 3.dp)
        )
    }
}


/** Below this, a stop is effectively where the passenger already is. */
private const val SAME_SPOT_METERS = 50.0

@Composable
private fun PickupDropoffInfo(pickup: MatchedStop?, dropoff: MatchedStop?) {
    val pickupText = pickup?.let {
        val stop = it.place.displayName()
        if (it.distanceMeters < SAME_SPOT_METERS) {
            stringResource(Res.string.search_result_pickup_here, stop)
        } else {
            stringResource(Res.string.search_result_pickup_distance, stop, formatDistance(it.distanceMeters))
        }
    }
    // Hidden when it's the passenger's own destination (the usual campus case), to avoid noise.
    val dropoffText = dropoff?.takeIf { it.distanceMeters >= SAME_SPOT_METERS }?.let {
        stringResource(Res.string.search_result_dropoff_distance, it.place.displayName(), formatDistance(it.distanceMeters))
    }
    val lines = listOfNotNull(pickupText, dropoffText)
    if (lines.isEmpty()) return

    Spacer(modifier = Modifier.height(Spacing.sm))
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        lines.forEach { line ->
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = vectorResource(Res.drawable.location_on_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(Spacing.xs))
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun Place.displayName(): String = name.ifBlank { address }

@Preview
@Composable
private fun TripResultCardPreview() {
    val stop = Place(name = "Parque de Envigado", address = "", latitude = 6.17, longitude = -75.585)
    CarpoolTheme {
        TripResultCard(
            result = TripResult(
                trip = Trip(
                    id = "t1",
                    routeId = "",
                    driverId = "d1",
                    vehicleId = "v1",
                    origin = stop,
                    destination = Place.EIA_LAS_PALMAS,
                    waypoints = emptyList(),
                    departureTime = 0L,
                    seatCount = 3,
                    contributionPerPassenger = 5_000,
                    status = TripStatus.Active,
                ),
                vehicle = null,
                availableSeats = 3,
                pickup = MatchedStop(stop, pathIndex = 0, distanceMeters = 350.0),
                dropoff = MatchedStop(Place.EIA_LAS_PALMAS, pathIndex = 1, distanceMeters = 0.0),
            ),
            onClick = {}
        )
    }
}
