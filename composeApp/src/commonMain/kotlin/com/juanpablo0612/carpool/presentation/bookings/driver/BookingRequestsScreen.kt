package com.juanpablo0612.carpool.presentation.bookings.driver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.bookings.driver.components.BookingRequestCard
import com.juanpablo0612.carpool.presentation.bookings.driver.components.ConfirmedBookingCard
import com.juanpablo0612.carpool.presentation.bookings.driver.components.HistoryBookingCard
import com.juanpablo0612.carpool.presentation.bookings.driver.components.RejectBottomSheet
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.booking_accepted_message
import enrutadoseia.composeapp.generated.resources.booking_requests_tab_confirmed
import enrutadoseia.composeapp.generated.resources.booking_requests_tab_history
import enrutadoseia.composeapp.generated.resources.booking_requests_tab_pending
import enrutadoseia.composeapp.generated.resources.booking_requests_title
import enrutadoseia.composeapp.generated.resources.booking_trip_now_full
import enrutadoseia.composeapp.generated.resources.confirmed_cancel_dialog_body
import enrutadoseia.composeapp.generated.resources.confirmed_cancel_dialog_button
import enrutadoseia.composeapp.generated.resources.confirmed_cancel_dialog_title
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
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BookingRequestsEvent.NavigateToPassengerProfile ->
                onNavigateToPassengerProfile(event.passengerId)
        }
    }

    val acceptedMsg = stringResource(Res.string.booking_accepted_message)
    val tripFullMsg = stringResource(Res.string.booking_trip_now_full)

    LaunchedEffect(state.snackbarMessage) {
        val msg = state.snackbarMessage ?: return@LaunchedEffect
        val displayMsg = when (msg) {
            BookingRequestsViewModel.TRIP_FULL_SIGNAL -> tripFullMsg
            else -> acceptedMsg
        }
        snackbarHostState.showSnackbar(displayMsg)
        viewModel.onAction(BookingRequestsAction.DismissSnackbar)
    }

    BookingRequestsContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingRequestsContent(
    state: BookingRequestsUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onAction: (BookingRequestsAction) -> Unit,
) {
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
        ConfirmDialog(
            title = stringResource(Res.string.confirmed_cancel_dialog_title),
            description = stringResource(Res.string.confirmed_cancel_dialog_body),
            confirmText = stringResource(Res.string.confirmed_cancel_dialog_button),
            onConfirm = { onAction(BookingRequestsAction.CancelConfirmed(state.cancelConfirmFor)) },
            onDismiss = { onAction(BookingRequestsAction.DismissCancelConfirmed) },
            isDestructive = true,
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.booking_requests_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            val tabs = BookingsTab.entries
            val selectedIndex = tabs.indexOf(state.tab)

            SecondaryTabRow(selectedTabIndex = selectedIndex) {
                Tab(
                    selected = state.tab == BookingsTab.Pending,
                    onClick = { onAction(BookingRequestsAction.SelectTab(BookingsTab.Pending)) },
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
                    selected = state.tab == BookingsTab.Confirmed,
                    onClick = { onAction(BookingRequestsAction.SelectTab(BookingsTab.Confirmed)) },
                    text = { Text(stringResource(Res.string.booking_requests_tab_confirmed)) },
                )
                Tab(
                    selected = state.tab == BookingsTab.History,
                    onClick = { onAction(BookingRequestsAction.SelectTab(BookingsTab.History)) },
                    text = { Text(stringResource(Res.string.booking_requests_tab_history)) },
                )
            }

            when {
                state.isLoading -> ListSkeleton(modifier = Modifier.fillMaxSize())
                else -> when (state.tab) {
                    BookingsTab.Pending -> TabContent(
                        items = state.pending,
                        emptyTitle = stringResource(Res.string.pending_empty_title),
                        emptySubtitle = stringResource(Res.string.pending_empty_subtitle),
                    ) { item ->
                        BookingRequestCard(
                            item = item,
                            processingIds = state.processingIds,
                            onAccept = { id, tripId ->
                                onAction(BookingRequestsAction.Accept(id, tripId))
                            },
                            onReject = { onAction(BookingRequestsAction.OpenReject(it)) },
                            onViewProfile = { onAction(BookingRequestsAction.OpenPassengerProfile(it)) },
                        )
                    }

                    BookingsTab.Confirmed -> TabContent(
                        items = state.confirmed,
                        emptyTitle = stringResource(Res.string.confirmed_empty_title),
                        emptySubtitle = stringResource(Res.string.confirmed_empty_subtitle),
                    ) { item ->
                        ConfirmedBookingCard(
                            item = item,
                            processingIds = state.processingIds,
                            onMessage = { /* chat: future screen */ },
                            onCancel = { onAction(BookingRequestsAction.OpenCancelConfirmed(item.booking.id)) },
                        )
                    }

                    BookingsTab.History -> TabContent(
                        items = state.history,
                        emptyTitle = stringResource(Res.string.history_empty_title),
                        emptySubtitle = stringResource(Res.string.history_empty_subtitle),
                    ) { item ->
                        HistoryBookingCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> TabContent(
    items: List<T>,
    emptyTitle: String,
    emptySubtitle: String,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit,
) {
    if (items.isEmpty()) {
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
        ) {
            items(items) { item -> itemContent(item) }
        }
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
