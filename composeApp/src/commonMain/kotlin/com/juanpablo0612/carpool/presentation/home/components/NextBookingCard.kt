package com.juanpablo0612.carpool.presentation.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.presentation.ui.util.relativeTime
import com.juanpablo0612.carpool.presentation.ui.components.BookingStatusBadge
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.home_next_booking_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun NextBookingCard(
    booking: Booking,
    now: Long,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HighlightCard(
        title = stringResource(Res.string.home_next_booking_title),
        statusBadge = { BookingStatusBadge(status = booking.status) },
        timeText = relativeTime(booking.departureTime, now),
        origin = booking.originName,
        destination = booking.destinationName,
        onClick = onTap,
        modifier = modifier,
    )
}
