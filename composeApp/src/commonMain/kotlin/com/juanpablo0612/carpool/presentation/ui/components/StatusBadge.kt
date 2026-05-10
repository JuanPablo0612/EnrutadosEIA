package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.booking_status_cancelled
import enrutadoseia.composeapp.generated.resources.booking_status_confirmed
import enrutadoseia.composeapp.generated.resources.booking_status_pending
import enrutadoseia.composeapp.generated.resources.booking_status_rejected
import enrutadoseia.composeapp.generated.resources.trip_status_active
import enrutadoseia.composeapp.generated.resources.trip_status_cancelled
import enrutadoseia.composeapp.generated.resources.trip_status_completed
import org.jetbrains.compose.resources.stringResource

@Composable
private fun StatusBadge(text: String, dotColor: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = dotColor.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(dotColor, CircleShape)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = dotColor
            )
        }
    }
}

@Composable
fun TripStatusBadge(status: TripStatus, modifier: Modifier = Modifier) {
    val extended = LocalExtendedColors.current
    val (text, color) = when (status) {
        TripStatus.Active -> stringResource(Res.string.trip_status_active) to extended.success
        TripStatus.Completed -> stringResource(Res.string.trip_status_completed) to MaterialTheme.colorScheme.outline
        TripStatus.Cancelled -> stringResource(Res.string.trip_status_cancelled) to MaterialTheme.colorScheme.error
    }
    StatusBadge(text = text, dotColor = color, modifier = modifier)
}

@Composable
fun BookingStatusBadge(status: BookingStatus, modifier: Modifier = Modifier) {
    val extended = LocalExtendedColors.current
    val (text, color) = when (status) {
        BookingStatus.Pending -> stringResource(Res.string.booking_status_pending) to extended.warning
        BookingStatus.Confirmed -> stringResource(Res.string.booking_status_confirmed) to extended.success
        BookingStatus.Rejected -> stringResource(Res.string.booking_status_rejected) to MaterialTheme.colorScheme.error
        BookingStatus.Cancelled -> stringResource(Res.string.booking_status_cancelled) to MaterialTheme.colorScheme.error
    }
    StatusBadge(text = text, dotColor = color, modifier = modifier)
}
