package com.juanpablo0612.carpool.presentation.trip.tracking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.trip.model.PickupStatus
import com.juanpablo0612.carpool.presentation.trip.tracking.PassengerWithStatus
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingAction
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingUiState
import com.juanpablo0612.carpool.presentation.trip.tracking.previewTrip
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.trip_tracking_complete_trip
import enrutadoseia.composeapp.generated.resources.trip_tracking_passengers_title
import org.jetbrains.compose.resources.stringResource

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
