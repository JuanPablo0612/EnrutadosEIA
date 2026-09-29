package com.juanpablo0612.carpool.presentation.ui.components

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
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
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
 * A status pill. Every status resolves to a paired `*Container`/`on*Container` role (from the
 * Material scheme or [LocalExtendedColors]), which keeps the label at 4.5:1 or better; a same-hue
 * foreground over an alpha tint of that hue would fail for several statuses.
 *
 * The dot and label are merged into one accessibility node via [Res.string.cd_status]. Status is
 * never colour-only — [text] always renders.
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
        shape = CircleShape,
        color = containerColor,
        modifier = modifier.clearAndSetSemantics { contentDescription = description }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = Spacing.xs),
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
            scheme.secondaryContainer,
            scheme.onSecondaryContainer,
        )

        TripStatus.InProgress -> Triple(
            stringResource(Res.string.trip_status_in_progress),
            scheme.primary,
            scheme.onPrimary,
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
    val extended = LocalExtendedColors.current
    val (text, container, content) = when (status) {
        BookingStatus.Pending -> Triple(
            stringResource(Res.string.booking_status_pending),
            extended.warningContainer,
            extended.onWarningContainer,
        )

        BookingStatus.Confirmed -> Triple(
            stringResource(Res.string.booking_status_confirmed),
            extended.successContainer,
            extended.onSuccessContainer,
        )

        BookingStatus.Rejected -> Triple(
            stringResource(Res.string.booking_status_rejected),
            scheme.errorContainer,
            scheme.onErrorContainer,
        )

        // Cancelled is an outcome, not a problem: neutral, so it doesn't shout like a rejection.
        BookingStatus.Cancelled -> Triple(
            stringResource(Res.string.booking_status_cancelled),
            scheme.surfaceVariant,
            scheme.onSurfaceVariant,
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
