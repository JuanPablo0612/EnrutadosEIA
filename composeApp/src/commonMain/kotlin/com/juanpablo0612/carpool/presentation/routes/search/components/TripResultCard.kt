package com.juanpablo0612.carpool.presentation.routes.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.routes.search.TripResult
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.RouteLineRow
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.utils.formatShortTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.time_am
import enrutadoseia.composeapp.generated.resources.time_pm
import enrutadoseia.composeapp.generated.resources.trip_available_seats
import enrutadoseia.composeapp.generated.resources.trip_contribution_free
import enrutadoseia.composeapp.generated.resources.trip_driver_placeholder
import enrutadoseia.composeapp.generated.resources.trip_result_view_detail
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

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
                text = formatDeparture(result.trip.departureTime),
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

@Composable
private fun formatDeparture(epochMs: Long): String {
    val local = Instant.fromEpochMilliseconds(epochMs)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    @Suppress("DEPRECATION")
    val day = local.dayOfMonth.toString().padStart(2, '0')
    @Suppress("DEPRECATION")
    val month = local.monthNumber.toString().padStart(2, '0')
    // Shared formatter rather than a sixth private copy; the hand-rolled version dropped AM/PM.
    val time = formatShortTime(
        hour = local.hour,
        minute = local.minute,
        amMarker = stringResource(Res.string.time_am),
        pmMarker = stringResource(Res.string.time_pm),
    )
    return "$day/$month · $time"
}
