package com.juanpablo0612.carpool.presentation.booking.driver

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.booking.asStringResource
import com.juanpablo0612.carpool.presentation.booking.driver.components.BookingRequestCard
import com.juanpablo0612.carpool.presentation.booking.driver.components.ConfirmedBookingCard
import com.juanpablo0612.carpool.presentation.booking.driver.components.RejectBottomSheet
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.confirmed_cancel_dialog_body
import enrutadoseia.composeapp.generated.resources.confirmed_cancel_dialog_button
import enrutadoseia.composeapp.generated.resources.confirmed_cancel_dialog_title
import enrutadoseia.composeapp.generated.resources.inbox_24px
import enrutadoseia.composeapp.generated.resources.trip_passengers_empty_subtitle
import enrutadoseia.composeapp.generated.resources.trip_passengers_empty_title
import enrutadoseia.composeapp.generated.resources.trip_passengers_section_confirmed
import enrutadoseia.composeapp.generated.resources.trip_passengers_section_pending
import enrutadoseia.composeapp.generated.resources.trip_passengers_title
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun TripPassengersScreen(
    viewModel: TripPassengersViewModel,
    onBackClick: () -> Unit,
    onNavigateToPassengerProfile: (String) -> Unit,
    onNavigateToRating: (bookingId: String, tripId: String, rateeId: String, rateeName: String) -> Unit,
    onNavigateToChat: (bookingId: String, otherPartyName: String, isReadOnly: Boolean) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is TripPassengersEvent.NavigateToPassengerProfile -> onNavigateToPassengerProfile(event.passengerId)
            is TripPassengersEvent.NavigateToRating ->
                onNavigateToRating(event.bookingId, event.tripId, event.rateeId, event.rateeName)
        }
    }

    TripPassengersContent(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
        onNavigateToChat = onNavigateToChat,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPassengersContent(
    state: TripPassengersUiState,
    onAction: (TripPassengersAction) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToChat: (bookingId: String, otherPartyName: String, isReadOnly: Boolean) -> Unit = { _, _, _ -> },
) {
    val nowMs = remember { Clock.System.now().toEpochMilliseconds() }

    if (state.pendingRejectionFor != null) {
        RejectBottomSheet(
            selectedReason = state.selectedRejectReason,
            comment = state.rejectComment,
            onSelectReason = { onAction(TripPassengersAction.SelectRejectReason(it)) },
            onCommentChange = { onAction(TripPassengersAction.UpdateRejectComment(it)) },
            onConfirm = { onAction(TripPassengersAction.ConfirmReject(state.pendingRejectionFor)) },
            onDismiss = { onAction(TripPassengersAction.DismissReject) },
        )
    }

    if (state.cancelConfirmFor != null) {
        ConfirmDialog(
            title = stringResource(Res.string.confirmed_cancel_dialog_title),
            description = stringResource(Res.string.confirmed_cancel_dialog_body),
            confirmText = stringResource(Res.string.confirmed_cancel_dialog_button),
            onConfirm = { onAction(TripPassengersAction.CancelConfirmed(state.cancelConfirmFor)) },
            onDismiss = { onAction(TripPassengersAction.DismissCancelConfirmed) },
            isDestructive = true,
        )
    }

    Scaffold(
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.trip_passengers_title),
                onBack = onBackClick,
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            state.error?.let { error ->
                ErrorMessage(
                    message = stringResource(error.asStringResource()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                        .clickable { onAction(TripPassengersAction.DismissError) },
                )
            }

            when {
                state.isLoading -> ListSkeleton(modifier = Modifier.fillMaxSize())
                state.pending.isEmpty() && state.confirmed.isEmpty() -> EmptyState(
                    icon = vectorResource(Res.drawable.inbox_24px),
                    title = stringResource(Res.string.trip_passengers_empty_title),
                    description = stringResource(Res.string.trip_passengers_empty_subtitle),
                    modifier = Modifier.fillMaxSize(),
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    if (state.pending.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(Res.string.trip_passengers_section_pending),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        items(state.pending, key = { "pending_${it.booking.id}" }) { item ->
                            BookingRequestCard(
                                item = item,
                                processingIds = state.processingIds,
                                onAccept = { id, tripId -> onAction(TripPassengersAction.Accept(id, tripId)) },
                                onReject = { onAction(TripPassengersAction.OpenReject(it)) },
                                onViewProfile = { onAction(TripPassengersAction.OpenPassengerProfile(it)) },
                            )
                        }
                    }

                    if (state.confirmed.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(Res.string.trip_passengers_section_confirmed),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        items(state.confirmed, key = { "confirmed_${it.booking.id}" }) { item ->
                            val isPast = item.booking.departureTime <= nowMs
                            ConfirmedBookingCard(
                                item = item,
                                processingIds = state.processingIds,
                                nowMs = nowMs,
                                onMessage = { onNavigateToChat(item.booking.id, item.passenger.name, isPast) },
                                onCancel = { onAction(TripPassengersAction.OpenCancelConfirmed(item.booking.id)) },
                                onRate = {
                                    onAction(
                                        TripPassengersAction.OnRateBooking(
                                            bookingId = item.booking.id,
                                            tripId = item.booking.tripId,
                                            rateeId = item.passenger.id,
                                            rateeName = item.passenger.name,
                                        )
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TripPassengersEmptyPreview() {
    CarpoolTheme {
        TripPassengersContent(
            state = TripPassengersUiState(isLoading = false),
            onAction = {},
            onBackClick = {},
        )
    }
}
