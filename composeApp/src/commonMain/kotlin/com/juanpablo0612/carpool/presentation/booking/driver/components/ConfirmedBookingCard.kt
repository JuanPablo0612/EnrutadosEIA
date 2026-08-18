package com.juanpablo0612.carpool.presentation.booking.driver.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.booking.model.BookingWithPassenger
import com.juanpablo0612.carpool.presentation.home.relativeTime
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.RouteLineRow
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.confirmed_booking_cancel_button
import enrutadoseia.composeapp.generated.resources.confirmed_booking_message_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmedBookingCard(
    item: BookingWithPassenger,
    processingIds: Set<String>,
    onMessage: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val booking = item.booking
    val passenger = item.passenger
    val isProcessing = booking.id in processingIds

    CarpoolListCard(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            UserAvatar(name = passenger.name, size = 40.dp)
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = passenger.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = relativeTime(booking.departureTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                RouteLineRow(
                    origin = booking.originName,
                    destination = booking.destinationName,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Spacer(modifier = Modifier.padding(top = Spacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            OutlinedButton(
                onClick = onMessage,
                modifier = Modifier.weight(1f),
            ) {
                Text(text = stringResource(Res.string.confirmed_booking_message_button))
            }
            OutlinedButton(
                onClick = onCancel,
                enabled = !isProcessing,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error),
                ),
                modifier = Modifier.weight(1f),
            ) {
                Text(text = stringResource(Res.string.confirmed_booking_cancel_button))
            }
        }
    }
}
