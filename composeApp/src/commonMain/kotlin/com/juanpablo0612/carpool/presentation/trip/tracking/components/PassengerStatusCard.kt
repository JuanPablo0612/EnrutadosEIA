package com.juanpablo0612.carpool.presentation.trip.tracking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.juanpablo0612.carpool.domain.trip.model.PickupStatus
import com.juanpablo0612.carpool.presentation.trip.tracking.PassengerWithStatus
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.trip_tracking_mark_dropped_off
import enrutadoseia.composeapp.generated.resources.trip_tracking_mark_picked_up
import enrutadoseia.composeapp.generated.resources.trip_tracking_message_passenger
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PassengerStatusCard(
    passenger: PassengerWithStatus,
    isProcessing: Boolean,
    onMarkPickedUp: () -> Unit,
    onMarkDroppedOff: () -> Unit,
    onMessage: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = passenger.passengerName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f)
                )
                PickupStatusChip(status = passenger.status)
            }

            Spacer(Modifier.height(Spacing.sm))

            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                // The driver had no way into a thread at all: the only chat entry point in the
                // app was the passenger-side button.
                OutlinedButton(
                    onClick = onMessage,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(Res.string.trip_tracking_message_passenger),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (passenger.status is PickupStatus.Waiting) {
                    OutlinedButton(
                        onClick = onMarkPickedUp,
                        enabled = !isProcessing,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(Res.string.trip_tracking_mark_picked_up),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (passenger.status is PickupStatus.PickedUp) {
                    Button(
                        onClick = onMarkDroppedOff,
                        enabled = !isProcessing,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(Res.string.trip_tracking_mark_dropped_off),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
