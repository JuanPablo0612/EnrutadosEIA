package com.juanpablo0612.carpool.presentation.trip.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.trip.model.PickupStatus
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.active_roles_close
import enrutadoseia.composeapp.generated.resources.arrow_back_24px
import enrutadoseia.composeapp.generated.resources.cancel_button
import enrutadoseia.composeapp.generated.resources.pickup_status_dropped_off
import enrutadoseia.composeapp.generated.resources.pickup_status_picked_up
import enrutadoseia.composeapp.generated.resources.trip_tracking_complete_confirm_body
import enrutadoseia.composeapp.generated.resources.trip_tracking_complete_confirm_button
import enrutadoseia.composeapp.generated.resources.trip_tracking_complete_confirm_title
import enrutadoseia.composeapp.generated.resources.trip_tracking_complete_trip
import enrutadoseia.composeapp.generated.resources.trip_tracking_mark_dropped_off
import enrutadoseia.composeapp.generated.resources.trip_tracking_mark_picked_up
import enrutadoseia.composeapp.generated.resources.trip_tracking_message_driver
import enrutadoseia.composeapp.generated.resources.trip_tracking_no_location
import enrutadoseia.composeapp.generated.resources.trip_tracking_passengers_title
import enrutadoseia.composeapp.generated.resources.trip_tracking_sos
import enrutadoseia.composeapp.generated.resources.trip_tracking_sos_call_emergency
import enrutadoseia.composeapp.generated.resources.trip_tracking_sos_report_issue
import enrutadoseia.composeapp.generated.resources.trip_tracking_sos_share_location
import enrutadoseia.composeapp.generated.resources.trip_tracking_sos_title
import enrutadoseia.composeapp.generated.resources.trip_tracking_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun TripTrackingScreen(
    viewModel: TripTrackingViewModel,
    onBackClick: () -> Unit,
    onNavigateToChat: (bookingId: String) -> Unit,
    onTripCompleted: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            TripTrackingEvent.NavigateBack -> onBackClick()
            TripTrackingEvent.TripCompleted -> onTripCompleted()
            is TripTrackingEvent.NavigateToChat -> onNavigateToChat(event.bookingId)
        }
    }

    TripTrackingContent(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripTrackingContent(
    state: TripTrackingUiState,
    onAction: (TripTrackingAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.trip_tracking_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(TripTrackingAction.OnBackClick) }) {
                        Icon(vectorResource(Res.drawable.arrow_back_24px), null)
                    }
                },
                actions = {
                    TextButton(
                        onClick = { onAction(TripTrackingAction.OnSOSClick) },
                        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = stringResource(Res.string.trip_tracking_sos),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.isDriver) {
            DriverTrackingContent(
                state = state,
                onAction = onAction,
                modifier = Modifier.padding(padding)
            )
        } else {
            PassengerTrackingContent(
                state = state,
                onAction = onAction,
                modifier = Modifier.padding(padding)
            )
        }

        if (state.showCompleteTripDialog) {
            CompleteTripDialog(
                onConfirm = { onAction(TripTrackingAction.OnCompleteTripConfirm) },
                onDismiss = { onAction(TripTrackingAction.OnCompleteTripDismiss) }
            )
        }

        if (state.showSosDialog) {
            SosDialog(onDismiss = { onAction(TripTrackingAction.OnSOSDismiss) })
        }
    }
}

@Composable
private fun DriverTrackingContent(
    state: TripTrackingUiState,
    onAction: (TripTrackingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                onMarkPickedUp = { onAction(TripTrackingAction.OnMarkPickedUp(passenger.passengerId)) },
                onMarkDroppedOff = { onAction(TripTrackingAction.OnMarkDroppedOff(passenger.passengerId)) }
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onAction(TripTrackingAction.OnCompleteTripClick) },
                enabled = !state.isCompletingTrip,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isCompletingTrip) {
                    CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                } else {
                    Text(stringResource(Res.string.trip_tracking_complete_trip))
                }
            }
        }
    }
}

@Composable
private fun PassengerTrackingContent(
    state: TripTrackingUiState,
    onAction: (TripTrackingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = state.trip?.origin?.name ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "→ ${state.trip?.destination?.name ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                val locationText = if (state.driverLatitude != null && state.driverLongitude != null) {
                    "%.5f, %.5f".format(state.driverLatitude, state.driverLongitude)
                } else {
                    stringResource(Res.string.trip_tracking_no_location)
                }
                Text(
                    text = locationText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (state.currentPassengerBookingId.isNotBlank()) {
            OutlinedButton(
                onClick = { onAction(TripTrackingAction.OnChatClick(state.currentPassengerBookingId)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.trip_tracking_message_driver))
            }
        }
    }
}

@Composable
private fun PassengerStatusCard(
    passenger: PassengerWithStatus,
    onMarkPickedUp: () -> Unit,
    onMarkDroppedOff: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
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

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (passenger.status is PickupStatus.Waiting) {
                    OutlinedButton(
                        onClick = onMarkPickedUp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(Res.string.trip_tracking_mark_picked_up),
                            maxLines = 1
                        )
                    }
                }
                if (passenger.status is PickupStatus.PickedUp) {
                    Button(
                        onClick = onMarkDroppedOff,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(Res.string.trip_tracking_mark_dropped_off),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PickupStatusChip(status: PickupStatus) {
    val (label, color) = when (status) {
        is PickupStatus.Waiting -> "" to MaterialTheme.colorScheme.outlineVariant
        is PickupStatus.PickedUp -> stringResource(Res.string.pickup_status_picked_up) to MaterialTheme.colorScheme.primary
        is PickupStatus.DroppedOff -> stringResource(Res.string.pickup_status_dropped_off) to MaterialTheme.colorScheme.secondary
    }
    if (label.isNotBlank()) {
        SuggestionChip(
            onClick = {},
            label = { Text(label) },
            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = color.copy(alpha = 0.15f))
        )
    }
}

@Composable
private fun CompleteTripDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.trip_tracking_complete_confirm_title)) },
        text = { Text(stringResource(Res.string.trip_tracking_complete_confirm_body)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(Res.string.trip_tracking_complete_confirm_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel_button))
            }
        }
    )
}

@Composable
private fun SosDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.trip_tracking_sos_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(Res.string.trip_tracking_sos_call_emergency))
                Text(stringResource(Res.string.trip_tracking_sos_share_location))
                Text(stringResource(Res.string.trip_tracking_sos_report_issue))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.active_roles_close)) }
        }
    )
}
