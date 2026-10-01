package com.juanpablo0612.carpool.presentation.trip.tracking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.presentation.place.add.components.MapPreview
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingAction
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingUiState
import com.juanpablo0612.carpool.presentation.trip.tracking.previewTrip
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.mediaPreviewSize
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.trip_tracking_message_driver
import enrutadoseia.composeapp.generated.resources.trip_tracking_no_location
import enrutadoseia.composeapp.generated.resources.trip_tracking_your_status
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PassengerTrackingContent(
    state: TripTrackingUiState,
    onAction: (TripTrackingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    // Scrolls: the card, the map and the chat button overflow a phone in landscape or at a large
    // font scale, and the chat button is the one thing the passenger must be able to reach.
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                Text(
                    text = state.trip?.origin?.name ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "→ ${state.trip?.destination?.name ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                val myStatus = state.passengers.find { it.bookingId == state.currentPassengerBookingId }?.status
                if (myStatus != null) {
                    Spacer(Modifier.height(Spacing.sm))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Weighted so a long label wraps instead of squeezing the status chip.
                        Text(
                            text = stringResource(Res.string.trip_tracking_your_status),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                        PickupStatusChip(status = myStatus)
                    }
                }

                Spacer(Modifier.height(Spacing.sm))
                val driverLatitude = state.driverLatitude
                val driverLongitude = state.driverLongitude
                if (driverLatitude != null && driverLongitude != null) {
                    MapPreview(
                        coordinates = Coordinates(driverLatitude, driverLongitude),
                        // Read-only: the passenger only observes the driver's position, so a
                        // dragged pin is discarded rather than fed back into any action.
                        onPinDragged = {},
                        modifier = Modifier.mediaPreviewSize(),
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.trip_tracking_no_location),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (state.currentPassengerBookingId.isNotBlank()) {
            OutlinedButton(
                onClick = {
                    onAction(
                        TripTrackingAction.OnChatClick(
                            bookingId = state.currentPassengerBookingId,
                            otherPartyName = state.driverName,
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.trip_tracking_message_driver))
            }
        }
    }
}

@Preview
@Composable
private fun PassengerTrackingContentPreview() {
    CarpoolTheme {
        PassengerTrackingContent(
            state = TripTrackingUiState(
                trip = previewTrip,
                isDriver = false,
                isLoading = false,
                currentPassengerBookingId = "b1",
                driverName = "Carlos Ruiz",
            ),
            onAction = {}
        )
    }
}
