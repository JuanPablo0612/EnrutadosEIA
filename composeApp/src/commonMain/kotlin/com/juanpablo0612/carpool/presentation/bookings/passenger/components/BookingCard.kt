package com.juanpablo0612.carpool.presentation.bookings.passenger.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.booking_status_cancelled
import enrutadoseia.composeapp.generated.resources.booking_status_confirmed
import enrutadoseia.composeapp.generated.resources.booking_status_pending
import enrutadoseia.composeapp.generated.resources.booking_status_rejected
import enrutadoseia.composeapp.generated.resources.cancel_booking_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun BookingCard(
    booking: Booking,
    onCancelClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.routeId,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = booking.vehicleId,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val (chipLabel, chipContainerColor) = when (booking.status) {
                    BookingStatus.Pending -> stringResource(Res.string.booking_status_pending) to
                            MaterialTheme.colorScheme.surfaceVariant
                    BookingStatus.Confirmed -> stringResource(Res.string.booking_status_confirmed) to
                            MaterialTheme.colorScheme.primaryContainer
                    BookingStatus.Rejected -> stringResource(Res.string.booking_status_rejected) to
                            MaterialTheme.colorScheme.errorContainer
                    BookingStatus.Cancelled -> stringResource(Res.string.booking_status_cancelled) to
                            MaterialTheme.colorScheme.errorContainer
                }

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = chipLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = chipContainerColor
                    )
                )
            }

            if (booking.status is BookingStatus.Pending) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { onCancelClick(booking.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(Res.string.cancel_booking_button))
                }
            }
        }
    }
}
