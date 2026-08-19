package com.juanpablo0612.carpool.presentation.trip.driverlist.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.presentation.trip.driverlist.TripWithStats
import com.juanpablo0612.carpool.presentation.ui.components.TripStatusBadge
import com.juanpablo0612.carpool.presentation.ui.theme.Elevation
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.utils.formatLongDate
import com.juanpablo0612.carpool.presentation.utils.formatShortTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.date_of_connector
import enrutadoseia.composeapp.generated.resources.day_names_short
import enrutadoseia.composeapp.generated.resources.month_names
import enrutadoseia.composeapp.generated.resources.time_am
import enrutadoseia.composeapp.generated.resources.time_pm
import enrutadoseia.composeapp.generated.resources.trip_action_start
import enrutadoseia.composeapp.generated.resources.trip_action_track
import enrutadoseia.composeapp.generated.resources.trip_cancel_confirm_button
import enrutadoseia.composeapp.generated.resources.trip_seats_occupied
import enrutadoseia.composeapp.generated.resources.trip_tracking_complete_trip
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DriverTripCard(
    tripWithStats: TripWithStats,
    onStartTrip: () -> Unit,
    onFinishTrip: () -> Unit,
    onTrackTrip: () -> Unit,
    onCancelTrip: () -> Unit,
    onViewPassengers: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trip = tripWithStats.trip
    val local = Instant.fromEpochMilliseconds(trip.departureTime)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val dayNamesShort = stringArrayResource(Res.array.day_names_short)
    val monthNames = stringArrayResource(Res.array.month_names)
    val dateConnector = stringResource(Res.string.date_of_connector)
    val amMarker = stringResource(Res.string.time_am)
    val pmMarker = stringResource(Res.string.time_pm)
    val timeStr = formatShortTime(local.hour, local.minute, amMarker, pmMarker)
    val dateStr = formatLongDate(local.year, local.monthNumber, local.dayOfMonth, dayNamesShort.toList(), monthNames.toList(), dateConnector)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.card)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$dateStr · $timeStr",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
                TripStatusBadge(status = trip.status)
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = "${trip.origin.name} → ${trip.destination.name}",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = stringResource(
                    Res.string.trip_seats_occupied,
                    tripWithStats.occupiedSeats,
                    trip.seatCount
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            tripWithStats.vehicle?.let { v ->
                Text(
                    text = "${v.brand} ${v.model} · ${v.licensePlate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // "Ver pasajeros" is intentionally not rendered here: its navigation callback is
            // still a no-op in MainNavGraph (no passenger management screen exists yet), so
            // showing it would be a dead tap. onViewPassengers stays wired below so the button
            // can be restored with a one-line change once that screen exists.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (trip.status) {
                    TripStatus.Active -> {
                        TextButton(
                            onClick = onCancelTrip,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(stringResource(Res.string.trip_cancel_confirm_button))
                        }
                        Button(
                            onClick = onStartTrip
                            // No explicit height: the previous 36dp clipped below Material's
                            // 40dp minimum touch target and at large font scales. Default sizing.
                        ) {
                            Text(
                                text = stringResource(Res.string.trip_action_start),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    TripStatus.InProgress -> {
                        TextButton(onClick = onFinishTrip) {
                            Text(stringResource(Res.string.trip_tracking_complete_trip))
                        }
                        Button(
                            onClick = onTrackTrip
                            // No explicit height: see rationale on the Start-trip button above.
                        ) {
                            Text(
                                text = stringResource(Res.string.trip_action_track),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    TripStatus.Completed, TripStatus.Cancelled -> Unit
                }
            }
        }
    }
}
