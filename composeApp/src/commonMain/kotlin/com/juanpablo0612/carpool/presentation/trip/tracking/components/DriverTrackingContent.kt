package com.juanpablo0612.carpool.presentation.trip.tracking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.trip.model.PickupStatus
import com.juanpablo0612.carpool.presentation.trip.tracking.PassengerWithStatus
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingAction
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingUiState
import com.juanpablo0612.carpool.presentation.trip.tracking.previewTrip
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.my_location_24px
import enrutadoseia.composeapp.generated.resources.trip_tracking_complete_trip
import enrutadoseia.composeapp.generated.resources.trip_tracking_passengers_title
import enrutadoseia.composeapp.generated.resources.trip_tracking_sharing_location
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun DriverTrackingContent(
    state: TripTrackingUiState,
    onAction: (TripTrackingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        if (state.isSharingLocation) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.my_location_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp) // icon-intrinsic size
                    )
                    Spacer(Modifier.width(Spacing.xs))
                    Text(
                        text = stringResource(Res.string.trip_tracking_sharing_location),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(Res.string.trip_tracking_passengers_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        items(state.passengers, key = { it.passengerId }) { passenger ->
            PassengerStatusCard(
                passenger = passenger,
                isProcessing = passenger.passengerId in state.processingPassengerIds,
                onMarkPickedUp = { onAction(TripTrackingAction.OnMarkPickedUp(passenger.passengerId)) },
                onMarkDroppedOff = { onAction(TripTrackingAction.OnMarkDroppedOff(passenger.passengerId)) },
                onMessage = {
                    onAction(
                        TripTrackingAction.OnChatClick(
                            bookingId = passenger.bookingId,
                            otherPartyName = passenger.passengerName,
                        )
                    )
                }
            )
        }

        item {
            Spacer(Modifier.height(Spacing.sm))
            Button(
                onClick = { onAction(TripTrackingAction.OnCompleteTripClick) },
                enabled = !state.isCompletingTrip && state.canCompleteTrip,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isCompletingTrip) {
                    CircularProgressIndicator(modifier = Modifier.padding(Spacing.xs))
                } else {
                    Text(stringResource(Res.string.trip_tracking_complete_trip))
                }
            }
        }
    }
}

@Preview
@Composable
private fun DriverTrackingContentPreview() {
    CarpoolTheme {
        DriverTrackingContent(
            state = TripTrackingUiState(
                trip = previewTrip,
                isDriver = true,
                isLoading = false,
                passengers = listOf(
                    PassengerWithStatus(
                        passengerId = "p1",
                        passengerName = "María López",
                        bookingId = "b1",
                        status = PickupStatus.Waiting
                    ),
                    PassengerWithStatus(
                        passengerId = "p2",
                        passengerName = "Juan Pérez",
                        bookingId = "b2",
                        status = PickupStatus.PickedUp
                    ),
                    PassengerWithStatus(
                        passengerId = "p3",
                        passengerName = "Ana Gómez",
                        bookingId = "b3",
                        status = PickupStatus.DroppedOff
                    ),
                )
            ),
            onAction = {}
        )
    }
}
