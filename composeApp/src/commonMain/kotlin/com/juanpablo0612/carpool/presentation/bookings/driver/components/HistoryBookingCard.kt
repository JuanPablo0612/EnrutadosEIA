package com.juanpablo0612.carpool.presentation.bookings.driver.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.booking.model.BookingWithPassenger
import com.juanpablo0612.carpool.presentation.home.relativeTime
import com.juanpablo0612.carpool.presentation.ui.components.BookingStatusBadge
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import org.jetbrains.compose.resources.vectorResource

@Composable
fun HistoryBookingCard(
    item: BookingWithPassenger,
    modifier: Modifier = Modifier,
) {
    val booking = item.booking
    val passenger = item.passenger

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
        ) {
            UserAvatar(name = passenger.name, size = 40.dp)
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = passenger.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    text = relativeTime(booking.departureTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = booking.originName,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                    )
                    Icon(
                        imageVector = vectorResource(Res.drawable.arrow_forward_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = Spacing.xs)
                            .size(12.dp),
                    )
                    Text(
                        text = booking.destinationName,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                    )
                }
            }
            Spacer(modifier = Modifier.width(Spacing.sm))
            BookingStatusBadge(status = booking.status)
        }
    }
}
