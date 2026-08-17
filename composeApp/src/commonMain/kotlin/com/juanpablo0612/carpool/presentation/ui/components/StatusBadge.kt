package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.presentation.ui.theme.Alpha
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.booking_status_cancelled
import enrutadoseia.composeapp.generated.resources.booking_status_confirmed
import enrutadoseia.composeapp.generated.resources.booking_status_pending
import enrutadoseia.composeapp.generated.resources.booking_status_rejected
import enrutadoseia.composeapp.generated.resources.cd_status
import enrutadoseia.composeapp.generated.resources.trip_status_cancelled
import enrutadoseia.composeapp.generated.resources.trip_status_completed
import enrutadoseia.composeapp.generated.resources.trip_status_in_progress
import enrutadoseia.composeapp.generated.resources.trip_status_scheduled
import org.jetbrains.compose.resources.stringResource

/**
 * Was `Text(color = dotColor)` on `Surface(color = dotColor.copy(alpha = 0.12f))` — foreground
 * and background were the same hue separated only by an alpha composite, which fails 4.5:1 for
 * several of the six statuses (`extended.warning` and `colorScheme.outline` most likely; see the
 * repo audit that prompted this rewrite). Every status now resolves to a real, paired M3
 * `*Container`/`on*Container` role — the same pattern already used correctly in [OfflineBanner] —
 * which Material's colour generation guarantees meets contrast on its own. The border adds a
 * visible edge independent of contrast, since a badge can render on a same-hue parent (e.g.
 * inside [HighlightCard]'s primary-container hero).
 *
 * The dot and label used to read as two sibling accessibility nodes stating the same fact; merged
 * into one via [Res.string.cd_status]. Status is still never colour-only — [text] always renders.
 */
@Composable
private fun StatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(Res.string.cd_status, text)
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = containerColor,
        border = BorderStroke(1.dp, contentColor.copy(alpha = Alpha.BADGE_CONTAINER)),
        modifier = modifier.clearAndSetSemantics { contentDescription = description }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(contentColor, CircleShape)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }
    }
}

@Composable
fun TripStatusBadge(status: TripStatus, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val (text, container, content) = when (status) {
        TripStatus.Active -> Triple(
            stringResource(Res.string.trip_status_scheduled),
            scheme.tertiaryContainer,
            scheme.onTertiaryContainer,
        )

        TripStatus.InProgress -> Triple(
            stringResource(Res.string.trip_status_in_progress),
            scheme.primaryContainer,
            scheme.onPrimaryContainer,
        )

        TripStatus.Completed -> Triple(
            stringResource(Res.string.trip_status_completed),
            scheme.surfaceVariant,
            scheme.onSurfaceVariant,
        )

        TripStatus.Cancelled -> Triple(
            stringResource(Res.string.trip_status_cancelled),
            scheme.errorContainer,
            scheme.onErrorContainer,
        )
    }
    StatusBadge(text = text, containerColor = container, contentColor = content, modifier = modifier)
}

@Composable
fun BookingStatusBadge(status: BookingStatus, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val (text, container, content) = when (status) {
        BookingStatus.Pending -> Triple(
            stringResource(Res.string.booking_status_pending),
            scheme.secondaryContainer,
            scheme.onSecondaryContainer,
        )

        BookingStatus.Confirmed -> Triple(
            stringResource(Res.string.booking_status_confirmed),
            scheme.primaryContainer,
            scheme.onPrimaryContainer,
        )

        BookingStatus.Rejected -> Triple(
            stringResource(Res.string.booking_status_rejected),
            scheme.errorContainer,
            scheme.onErrorContainer,
        )

        BookingStatus.Cancelled -> Triple(
            stringResource(Res.string.booking_status_cancelled),
            scheme.errorContainer,
            scheme.onErrorContainer,
        )
    }
    StatusBadge(text = text, containerColor = container, contentColor = content, modifier = modifier)
}

private val allTripStatuses = listOf(TripStatus.Active, TripStatus.InProgress, TripStatus.Completed, TripStatus.Cancelled)
private val allBookingStatuses = listOf(BookingStatus.Pending, BookingStatus.Confirmed, BookingStatus.Rejected, BookingStatus.Cancelled)

@Preview
@Composable
private fun TripStatusBadgePreview() {
    CarpoolTheme {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            allTripStatuses.forEach { TripStatusBadge(status = it) }
        }
    }
}

@Preview
@Composable
private fun BookingStatusBadgePreview() {
    CarpoolTheme {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            allBookingStatuses.forEach { BookingStatusBadge(status = it) }
        }
    }
}
