package com.juanpablo0612.carpool.presentation.booking.driver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.booking.asStringResource
import com.juanpablo0612.carpool.presentation.booking.driver.components.BookingCancelConfirmDialog
import com.juanpablo0612.carpool.presentation.booking.driver.components.HistoryBookingCard
import com.juanpablo0612.carpool.presentation.booking.driver.components.RejectBottomSheet
import com.juanpablo0612.carpool.presentation.booking.driver.components.confirmedBookingItems
import com.juanpablo0612.carpool.presentation.booking.driver.components.pendingBookingItems
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.rememberNowMs
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.booking_requests_tab_confirmed
import enrutadoseia.composeapp.generated.resources.booking_requests_tab_history
import enrutadoseia.composeapp.generated.resources.booking_requests_tab_pending
import enrutadoseia.composeapp.generated.resources.booking_requests_title
import enrutadoseia.composeapp.generated.resources.booking_trip_now_full
import enrutadoseia.composeapp.generated.resources.confirmed_empty_subtitle
import enrutadoseia.composeapp.generated.resources.confirmed_empty_title
import enrutadoseia.composeapp.generated.resources.history_empty_subtitle
import enrutadoseia.composeapp.generated.resources.history_empty_title
import enrutadoseia.composeapp.generated.resources.inbox_24px
import enrutadoseia.composeapp.generated.resources.pending_empty_subtitle
import enrutadoseia.composeapp.generated.resources.pending_empty_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BookingRequestsScreen(
    viewModel: BookingRequestsViewModel,
    onNavigateToPassengerProfile: (String) -> Unit = {},
    onNavigateToRating: (bookingId: String, tripId: String, rateeId: String, rateeName: String) -> Unit = { _, _, _, _ -> },
    onNavigateToChat: (bookingId: String, tripId: String, otherPartyName: String, isReadOnly: Boolean) -> Unit = { _, _, _, _ -> },
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BookingRequestsEvent.NavigateToPassengerProfile ->
                onNavigateToPassengerProfile(event.passengerId)
            is BookingRequestsEvent.NavigateToRating ->
                onNavigateToRating(event.bookingId, event.tripId, event.rateeId, event.rateeName)
        }
    }

    BookingRequestsContent(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToChat = onNavigateToChat,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingRequestsContent(
    state: BookingRequestsUiState,
    onAction: (BookingRequestsAction) -> Unit,
    onNavigateToChat: (bookingId: String, tripId: String, otherPartyName: String, isReadOnly: Boolean) -> Unit = { _, _, _, _ -> },
) {
    val nowMs = rememberNowMs()
    if (state.pendingRejectionFor != null) {
        RejectBottomSheet(
            selectedReason = state.selectedRejectReason,
            comment = state.rejectComment,
            onSelectReason = { onAction(BookingRequestsAction.SelectRejectReason(it)) },
            onCommentChange = { onAction(BookingRequestsAction.UpdateRejectComment(it)) },
            onConfirm = { onAction(BookingRequestsAction.ConfirmReject(state.pendingRejectionFor)) },
            onDismiss = { onAction(BookingRequestsAction.DismissReject) },
        )
    }

    if (state.cancelConfirmFor != null) {
        BookingCancelConfirmDialog(
            onConfirm = { onAction(BookingRequestsAction.CancelConfirmed(state.cancelConfirmFor)) },
            onDismiss = { onAction(BookingRequestsAction.DismissCancelConfirmed) },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.booking_requests_title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (state.tripJustFilled) {
                TripFilledBanner(
                    onDismiss = { onAction(BookingRequestsAction.DismissTripFilledNotice) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                )
            }

            state.error?.let { error ->
                ErrorMessage(
                    message = stringResource(error.asStringResource()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                        .clickable { onAction(BookingRequestsAction.DismissError) },
                )
            }

            val tabs = DriverBookingsTab.entries
            val selectedIndex = tabs.indexOf(state.tab)

            SecondaryTabRow(selectedTabIndex = selectedIndex) {
                Tab(
                    selected = state.tab == DriverBookingsTab.Pending,
                    onClick = { onAction(BookingRequestsAction.SelectTab(DriverBookingsTab.Pending)) },
                    text = {
                        BadgedBox(
                            badge = {
                                if (state.pending.isNotEmpty()) {
                                    Badge { Text(state.pending.size.toString()) }
                                }
                            },
                        ) {
                            Text(stringResource(Res.string.booking_requests_tab_pending))
                        }
                    },
                )
                Tab(
                    selected = state.tab == DriverBookingsTab.Confirmed,
                    onClick = { onAction(BookingRequestsAction.SelectTab(DriverBookingsTab.Confirmed)) },
                    text = { Text(stringResource(Res.string.booking_requests_tab_confirmed)) },
                )
                Tab(
                    selected = state.tab == DriverBookingsTab.History,
                    onClick = { onAction(BookingRequestsAction.SelectTab(DriverBookingsTab.History)) },
                    text = { Text(stringResource(Res.string.booking_requests_tab_history)) },
                )
            }

            when {
                state.isLoading -> ListSkeleton(modifier = Modifier.fillMaxSize())
                else -> when (state.tab) {
                    DriverBookingsTab.Pending -> TabContent(
                        isEmpty = state.pending.isEmpty(),
                        emptyTitle = stringResource(Res.string.pending_empty_title),
                        emptySubtitle = stringResource(Res.string.pending_empty_subtitle),
                    ) {
                        pendingBookingItems(
                            items = state.pending,
                            processingIds = state.processingIds,
                            nowMs = nowMs,
                            key = { it.booking.id },
                            onAccept = { id, tripId -> onAction(BookingRequestsAction.Accept(id, tripId)) },
                            onReject = { onAction(BookingRequestsAction.OpenReject(it)) },
                            onViewProfile = { onAction(BookingRequestsAction.OpenPassengerProfile(it)) },
                        )
                    }

                    DriverBookingsTab.Confirmed -> TabContent(
                        isEmpty = state.confirmed.isEmpty(),
                        emptyTitle = stringResource(Res.string.confirmed_empty_title),
                        emptySubtitle = stringResource(Res.string.confirmed_empty_subtitle),
                    ) {
                        confirmedBookingItems(
                            items = state.confirmed,
                            processingIds = state.processingIds,
                            nowMs = nowMs,
                            key = { it.booking.id },
                            onMessage = { item, isPast ->
                                onNavigateToChat(item.booking.id, item.booking.tripId, item.passenger.name, isPast)
                            },
                            onCancel = { onAction(BookingRequestsAction.OpenCancelConfirmed(it)) },
                            onRate = { item ->
                                onAction(
                                    BookingRequestsAction.OnRateBooking(
                                        bookingId = item.booking.id,
                                        tripId = item.booking.tripId,
                                        rateeId = item.passenger.id,
                                        rateeName = item.passenger.name,
                                    )
                                )
                            },
                        )
                    }

                    DriverBookingsTab.History -> TabContent(
                        isEmpty = state.history.isEmpty(),
                        emptyTitle = stringResource(Res.string.history_empty_title),
                        emptySubtitle = stringResource(Res.string.history_empty_subtitle),
                    ) {
                        items(state.history, key = { it.booking.id }) { item ->
                            HistoryBookingCard(item = item)
                        }
                    }
                }
            }
        }
    }
}

// Inline, dismiss-on-tap notice — CLAUDE.md forbids SnackBars, so the "trip is now full" signal
// (previously smuggled through a snackbar string) is rendered as a real, non-transient banner.
@Composable
private fun TripFilledBanner(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.clickable(onClick = onDismiss),
    ) {
        Text(
            text = stringResource(Res.string.booking_trip_now_full),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

@Composable
private fun TabContent(
    isEmpty: Boolean,
    emptyTitle: String,
    emptySubtitle: String,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    if (isEmpty) {
        EmptyState(
            icon = vectorResource(Res.drawable.inbox_24px),
            title = emptyTitle,
            description = emptySubtitle,
            modifier = modifier.fillMaxSize(),
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            content = content,
        )
    }
}

@Preview
@Composable
private fun BookingRequestsEmptyPreview() {
    CarpoolTheme {
        BookingRequestsContent(
            state = BookingRequestsUiState(isLoading = false),
            onAction = {},
        )
    }
}
