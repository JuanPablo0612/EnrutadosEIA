package com.juanpablo0612.carpool.presentation.bookings.passenger.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import enrutadoseia.composeapp.generated.resources.booking_status_cancelled
import enrutadoseia.composeapp.generated.resources.booking_status_confirmed
import enrutadoseia.composeapp.generated.resources.booking_status_pending
import enrutadoseia.composeapp.generated.resources.booking_status_rejected
import enrutadoseia.composeapp.generated.resources.cancel_booking_button
import enrutadoseia.composeapp.generated.resources.departure_time_label
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BookingCard(
    booking: Booking,
    onCancelClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = booking.originName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_forward_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp).size(16.dp)
                        )
                        Text(
                            text = booking.destinationName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            Res.string.departure_time_label,
                            formatDepartureTime(booking.departureTime)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
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
                    colors = AssistChipDefaults.assistChipColors(containerColor = chipContainerColor)
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

private fun formatDepartureTime(epochMs: Long): String {
    val local = Instant.fromEpochMilliseconds(epochMs)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = local.hour.toString().padStart(2, '0')
    val minute = local.minute.toString().padStart(2, '0')
    @Suppress("DEPRECATION")
    val day = local.dayOfMonth.toString().padStart(2, '0')
    @Suppress("DEPRECATION")
    val month = local.monthNumber.toString().padStart(2, '0')
    return "$hour:$minute - $day/$month/${local.year}"
}
