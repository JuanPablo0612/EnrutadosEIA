package com.juanpablo0612.carpool.presentation.trip.passengerdetail.components

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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.trip.model.TripClosedReason
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.info_24px
import enrutadoseia.composeapp.generated.resources.schedule_24px
import enrutadoseia.composeapp.generated.resources.trip_closed_cancelled
import enrutadoseia.composeapp.generated.resources.trip_closed_departed
import enrutadoseia.composeapp.generated.resources.trip_closed_finished
import enrutadoseia.composeapp.generated.resources.trip_closed_no_bookings
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Why a passenger can no longer ask for a seat on this trip, near the top so it is read before
 * the details. Announced politely: a trip left open can close while the passenger is looking.
 */
@Composable
internal fun TripClosedBanner(reason: TripClosedReason, modifier: Modifier = Modifier) {
    val (icon, title) = when (reason) {
        TripClosedReason.Cancelled -> Res.drawable.info_24px to Res.string.trip_closed_cancelled
        TripClosedReason.Departed -> Res.drawable.schedule_24px to Res.string.trip_closed_departed
        TripClosedReason.Finished -> Res.drawable.schedule_24px to Res.string.trip_closed_finished
    }
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = vectorResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp).size(20.dp),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = stringResource(Res.string.trip_closed_no_bookings),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview
@Composable
private fun TripClosedBannerPreview() {
    CarpoolTheme {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm), modifier = Modifier.padding(Spacing.lg)) {
            TripClosedBanner(reason = TripClosedReason.Cancelled)
            TripClosedBanner(reason = TripClosedReason.Departed)
            TripClosedBanner(reason = TripClosedReason.Finished)
        }
    }
}
