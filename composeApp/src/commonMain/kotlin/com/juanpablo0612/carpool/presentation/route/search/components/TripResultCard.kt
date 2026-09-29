package com.juanpablo0612.carpool.presentation.route.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.MatchedStop
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripDriver
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.model.TripVehicle
import com.juanpablo0612.carpool.presentation.route.search.TripResult
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.contributionLabel
import com.juanpablo0612.carpool.presentation.ui.util.departureDayLabel
import com.juanpablo0612.carpool.presentation.ui.util.formatTime
import com.juanpablo0612.carpool.presentation.ui.util.rememberNowMs
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.location_on_24px
import enrutadoseia.composeapp.generated.resources.search_result_day_and_origin
import enrutadoseia.composeapp.generated.resources.search_result_drops_off
import enrutadoseia.composeapp.generated.resources.search_result_last_seat
import enrutadoseia.composeapp.generated.resources.search_result_picks_up
import enrutadoseia.composeapp.generated.resources.trip_available_seats
import enrutadoseia.composeapp.generated.resources.trip_driver_placeholder
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.Clock

/**
 * One search result: when it leaves, what it costs and how many seats are left, where it meets
 * the passenger, and who drives in what. The whole card opens the trip.
 */
@Composable
fun TripResultCard(
    result: TripResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    now: Long = rememberNowMs(),
) {
    val trip = result.trip
    CarpoolListCard(modifier = modifier, onClick = onClick) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = formatTime(trip.departureTime), style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = stringResource(
                            Res.string.search_result_day_and_origin,
                            departureDayLabel(trip.departureTime, now),
                            trip.origin.name,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = contributionLabel(trip.contributionPerPassenger),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    SeatsLeft(seats = result.availableSeats)
                }
            }
            MeetingPoint(result)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DriverRow(driver = trip.driver, vehicle = trip.vehicle)
        }
    }
}

@Composable
private fun SeatsLeft(seats: Int) {
    if (seats == 1) {
        Text(
            text = stringResource(Res.string.search_result_last_seat),
            style = MaterialTheme.typography.titleSmall,
            color = LocalExtendedColors.current.warning,
        )
    } else {
        Text(
            text = stringResource(Res.string.trip_available_seats, seats),
            style = MaterialTheme.typography.titleSmall,
            color = LocalExtendedColors.current.success,
        )
    }
}

/**
 * Where the trip meets the passenger: the stop that picks them up going to campus, or drops them
 * off leaving it. It names the stop and never a distance, since every stop is inside the EIA
 * community's own routes.
 */
@Composable
private fun MeetingPoint(result: TripResult) {
    val text = when {
        result.pickup != null && !result.pickup.place.isCampusPreset ->
            stringResource(Res.string.search_result_picks_up, result.pickup.place.name)
        result.dropoff != null && !result.dropoff.place.isCampusPreset ->
            stringResource(Res.string.search_result_drops_off, result.dropoff.place.name)
        else -> return
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Icon(
            imageVector = vectorResource(Res.drawable.location_on_24px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
        )
        Text(text = text, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun DriverRow(driver: TripDriver, vehicle: TripVehicle) {
    val name = driver.name.ifBlank { stringResource(Res.string.trip_driver_placeholder) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        UserAvatar(name = name, photoUrl = driver.photoUrl, size = 36.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            vehicle.describe()?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}

/** "Mazda 3 · Gris", or null when the snapshot is empty. */
internal fun TripVehicle.describe(): String? =
    listOf("$brand $model".trim(), color).filter { it.isNotBlank() }.joinToString(" · ").ifBlank { null }

private val previewNow = Clock.System.now().toEpochMilliseconds()

@Preview
@Composable
private fun TripResultCardPreview() {
    val pickup = Place(name = "Parque de Envigado", address = "", latitude = 6.17, longitude = -75.58)
    CarpoolTheme {
        TripResultCard(
            result = TripResult(
                trip = Trip(
                    id = "t1",
                    routeId = "",
                    driverId = "d1",
                    vehicleId = "v1",
                    driver = TripDriver(name = "Carolina Restrepo"),
                    vehicle = TripVehicle(brand = "Mazda", model = "3", color = "Gris"),
                    origin = Place(name = "Viva Envigado", address = "", latitude = 6.17, longitude = -75.59),
                    destination = Place.EIA_LAS_PALMAS,
                    waypoints = listOf(pickup),
                    departureTime = previewNow + 3_600_000L,
                    seatCount = 3,
                    contributionPerPassenger = 4_000,
                    status = TripStatus.Active,
                ),
                availableSeats = 1,
                pickup = MatchedStop(pickup, pathIndex = 1, distanceMeters = 150.0),
            ),
            onClick = {},
            now = previewNow,
        )
    }
}
