package com.juanpablo0612.carpool.presentation.trip.tracking.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.trip.model.PickupStatus
import com.juanpablo0612.carpool.presentation.trip.tracking.PassengerWithStatus
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingAction
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingUiState
import com.juanpablo0612.carpool.presentation.trip.tracking.previewTrip
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ScreenPreviews
import com.juanpablo0612.carpool.presentation.ui.util.WindowLayout
import com.juanpablo0612.carpool.presentation.ui.util.rememberWindowLayout
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.passenger_dropped_off_confirm_body
import enrutadoseia.composeapp.generated.resources.passenger_dropped_off_confirm_button
import enrutadoseia.composeapp.generated.resources.passenger_dropped_off_confirm_title
import org.jetbrains.compose.resources.stringResource

/**
 * The driver's view of a trip in progress: the map stays on screen while they manage passengers,
 * in a bottom sheet over it on phones and in a panel beside it on wider windows.
 */
@Composable
internal fun DriverTrackingContent(
    state: TripTrackingUiState,
    onAction: (TripTrackingAction) -> Unit,
    modifier: Modifier = Modifier,
    layout: WindowLayout = rememberWindowLayout(),
) {
    state.pendingDropOffPassengerId?.let {
        ConfirmDialog(
            title = stringResource(Res.string.passenger_dropped_off_confirm_title),
            description = stringResource(Res.string.passenger_dropped_off_confirm_body),
            confirmText = stringResource(Res.string.passenger_dropped_off_confirm_button),
            onConfirm = { onAction(TripTrackingAction.OnConfirmDropOff) },
            onDismiss = { onAction(TripTrackingAction.OnDismissDropOffConfirm) }
        )
    }

    // A phone in landscape is too short for a sheet over the map, so it gets the panel as well.
    if (layout.isWidthCompact) {
        DriverTrackingSheetLayout(state = state, onAction = onAction, modifier = modifier)
    } else {
        Row(modifier = modifier.fillMaxSize()) {
            TripTrackingMap(
                trip = state.trip,
                driverPosition = state.driverPosition,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            ) {
                DriverPassengerPanel(state = state, onAction = onAction, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DriverTrackingSheetLayout(
    state: TripTrackingUiState,
    onAction: (TripTrackingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomSheetScaffold(
        scaffoldState = rememberBottomSheetScaffoldState(),
        sheetPeekHeight = SheetPeekHeight,
        sheetContent = {
            DriverPassengerPanel(
                state = state,
                onAction = onAction,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = Spacing.lg,
                    end = Spacing.lg,
                    bottom = Spacing.lg,
                ),
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        // Padded by the peek height so the map centres its markers in the part left uncovered.
        TripTrackingMap(
            trip = state.trip,
            driverPosition = state.driverPosition,
            modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding()),
        )
    }
}

/** Enough for the drag handle, the header and the top of the first passenger card. */
private val SheetPeekHeight = 220.dp

private val previewPassengers = listOf(
    PassengerWithStatus(passengerId = "p1", passengerName = "María López", bookingId = "b1", status = PickupStatus.Waiting),
    PassengerWithStatus(passengerId = "p2", passengerName = "Juan Pérez", bookingId = "b2", status = PickupStatus.PickedUp),
    PassengerWithStatus(passengerId = "p3", passengerName = "Ana Gómez", bookingId = "b3", status = PickupStatus.DroppedOff),
)

@ScreenPreviews
@Composable
private fun DriverTrackingContentPreview() {
    CarpoolTheme {
        DriverTrackingContent(
            state = TripTrackingUiState(
                trip = previewTrip,
                isDriver = true,
                isLoading = false,
                isSharingLocation = true,
                passengers = previewPassengers,
            ),
            onAction = {}
        )
    }
}
