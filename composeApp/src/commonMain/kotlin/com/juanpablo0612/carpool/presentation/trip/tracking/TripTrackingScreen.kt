package com.juanpablo0612.carpool.presentation.trip.tracking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.presentation.trip.tracking.components.CompleteTripDialog
import com.juanpablo0612.carpool.presentation.trip.tracking.components.DriverTrackingContent
import com.juanpablo0612.carpool.presentation.trip.tracking.components.PassengerTrackingContent
import com.juanpablo0612.carpool.presentation.trip.tracking.components.SosDialog
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.trip_tracking_sos
import enrutadoseia.composeapp.generated.resources.trip_tracking_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun TripTrackingScreen(
    viewModel: TripTrackingViewModel,
    onBackClick: () -> Unit,
    onNavigateToChat: (bookingId: String, otherPartyName: String, isReadOnly: Boolean) -> Unit,
    onTripCompleted: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            TripTrackingEvent.NavigateBack -> onBackClick()
            TripTrackingEvent.TripCompleted -> onTripCompleted()
            is TripTrackingEvent.NavigateToChat ->
                onNavigateToChat(event.bookingId, event.otherPartyName, event.isReadOnly)
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
            CarpoolBackTopBar(
                title = stringResource(Res.string.trip_tracking_title),
                onBack = { onAction(TripTrackingAction.OnBackClick) },
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
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            state.error?.let { error ->
                ErrorMessage(
                    message = stringResource(error.asStringResource()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                        .clickable { onAction(TripTrackingAction.OnErrorDismissed) }
                )
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (state.isLoading) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (state.isDriver) {
                    DriverTrackingContent(state = state, onAction = onAction)
                } else {
                    PassengerTrackingContent(state = state, onAction = onAction)
                }
            }
        }

        if (state.showCompleteTripDialog) {
            CompleteTripDialog(
                onConfirm = { onAction(TripTrackingAction.OnCompleteTripConfirm) },
                onDismiss = { onAction(TripTrackingAction.OnCompleteTripDismiss) }
            )
        }

        if (state.showSosDialog) {
            SosDialog(
                vibrateSosEnabled = state.vibrateSosEnabled,
                noContactsMessageVisible = state.sosNoContacts,
                locationSharedMessageVisible = state.sosLocationShared,
                onCallEmergency = { onAction(TripTrackingAction.OnSOSCallEmergencyClick) },
                onShareLocation = { onAction(TripTrackingAction.OnSOSShareLocationClick) },
                onDismiss = { onAction(TripTrackingAction.OnSOSDismiss) }
            )
        }
    }
}

internal val previewTrip = Trip(
    id = "t1",
    routeId = "r1",
    driverId = "d1",
    vehicleId = "v1",
    origin = com.juanpablo0612.carpool.domain.place.model.Place(
        name = "Casa",
        address = "Calle 10 #20-30",
        latitude = 6.2,
        longitude = -75.6
    ),
    destination = com.juanpablo0612.carpool.domain.place.model.Place.UNIVERSITY_EIA,
    waypoints = emptyList(),
    departureTime = 0L,
    status = TripStatus.InProgress,
    driverLatitude = 6.1633,
    driverLongitude = -75.4913,
)
