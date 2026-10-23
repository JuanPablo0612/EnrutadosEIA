package com.juanpablo0612.carpool.presentation.trip.tracking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingAction
import com.juanpablo0612.carpool.presentation.trip.tracking.TripTrackingUiState
import com.juanpablo0612.carpool.presentation.trip.tracking.previewTrip
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ScreenPreviews
import com.juanpablo0612.carpool.presentation.ui.util.WindowLayout
import com.juanpablo0612.carpool.presentation.ui.util.centeredContent
import com.juanpablo0612.carpool.presentation.ui.util.mediaPreviewSize
import com.juanpablo0612.carpool.presentation.ui.util.rememberWindowLayout
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.booking_action_pay
import enrutadoseia.composeapp.generated.resources.trip_tracking_message_driver
import enrutadoseia.composeapp.generated.resources.trip_tracking_locating_driver
import enrutadoseia.composeapp.generated.resources.trip_tracking_your_status
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PassengerTrackingContent(
    state: TripTrackingUiState,
    onAction: (TripTrackingAction) -> Unit,
    modifier: Modifier = Modifier,
    layout: WindowLayout = rememberWindowLayout(),
) {
    // Wide or short windows give the map the full height beside the details.
    val mapBeside = layout.prefersTwoPanes

    Row(modifier = modifier.fillMaxSize()) {
        if (mapBeside) {
            PassengerMap(
                state = state,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = Spacing.lg, top = Spacing.lg, bottom = Spacing.lg)
                    .clip(MaterialTheme.shapes.medium),
            )
        }
        // Scrolls: the card, the map and the chat button overflow a phone in landscape or at a
        // large font scale, and the chat button is the one thing the passenger must be able to reach.
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .centeredContent(ContentWidth.list)
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

                    if (!mapBeside) {
                        Spacer(Modifier.height(Spacing.sm))
                        PassengerMap(state = state, modifier = Modifier.mediaPreviewSize())
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
                // Riding is when most passengers pay, so the driver's methods are one tap away.
                Button(
                    onClick = { onAction(TripTrackingAction.OnPayDriverClick) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.booking_action_pay))
                }
            }
        }
    }
}

@Composable
private fun PassengerMap(state: TripTrackingUiState, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        TripTrackingMap(trip = state.trip, driverPosition = state.driverPosition, modifier = Modifier.fillMaxSize())
        if (state.isLocatingDriver) {
            LocatingDriverIndicator(modifier = Modifier.align(Alignment.TopCenter).padding(Spacing.sm))
        }
    }
}

@Composable
private fun LocatingDriverIndicator(modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 2.dp,
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            Text(
                text = stringResource(Res.string.trip_tracking_locating_driver),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@ScreenPreviews
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
