package com.juanpablo0612.carpool.presentation.booking.passenger

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.presentation.booking.asStringResource
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.booking.passenger.components.BookingDateGroupHeader
import com.juanpablo0612.carpool.presentation.booking.passenger.components.EnrichedBookingCard
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.RelativeDateGroup
import com.juanpablo0612.carpool.presentation.ui.util.groupByRelativeDate
import com.juanpablo0612.carpool.presentation.ui.util.rememberNowMs
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.bookings_past_empty_subtitle
import enrutadoseia.composeapp.generated.resources.bookings_past_empty_title
import enrutadoseia.composeapp.generated.resources.bookings_past_search_no_results
import enrutadoseia.composeapp.generated.resources.bookings_past_search_placeholder
import enrutadoseia.composeapp.generated.resources.bookings_search_trips_action
import enrutadoseia.composeapp.generated.resources.bookings_tab_past
import enrutadoseia.composeapp.generated.resources.bookings_tab_upcoming
import enrutadoseia.composeapp.generated.resources.bookings_upcoming_empty_subtitle
import enrutadoseia.composeapp.generated.resources.bookings_upcoming_empty_title
import enrutadoseia.composeapp.generated.resources.bookmarks_24px
import enrutadoseia.composeapp.generated.resources.cancel_confirm_body
import enrutadoseia.composeapp.generated.resources.cancel_confirm_button
import enrutadoseia.composeapp.generated.resources.cancel_confirm_title
import enrutadoseia.composeapp.generated.resources.cancel_pending_confirm_body
import enrutadoseia.composeapp.generated.resources.cancel_pending_confirm_button
import enrutadoseia.composeapp.generated.resources.cancel_pending_confirm_title
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun PassengerBookingsScreen(
    viewModel: PassengerBookingsViewModel,
    onBackClick: () -> Unit,
    onNavigateToTripTracking: (String) -> Unit = {},
    onNavigateToRating: (bookingId: String, tripId: String, rateeId: String, rateeName: String) -> Unit = { _, _, _, _ -> },
    onNavigateToSearchTrips: () -> Unit = {},
    onNavigateToChat: (bookingId: String, tripId: String, otherPartyName: String, isReadOnly: Boolean) -> Unit = { _, _, _, _ -> },
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            PassengerBookingsEvent.NavigateBack -> onBackClick()
            is PassengerBookingsEvent.NavigateToTripTracking -> onNavigateToTripTracking(event.tripId)
            is PassengerBookingsEvent.NavigateToRating -> onNavigateToRating(
                event.bookingId, event.tripId, event.rateeId, event.rateeName
            )
            PassengerBookingsEvent.NavigateToSearchTrips -> onNavigateToSearchTrips()
            is PassengerBookingsEvent.NavigateToChat ->
                onNavigateToChat(event.bookingId, event.tripId, event.otherPartyName, event.isReadOnly)
        }
    }

    PassengerBookingsContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerBookingsContent(
    state: PassengerBookingsUiState,
    onAction: (PassengerBookingsAction) -> Unit
) {
    val nowMs = rememberNowMs()
    // A booking belongs to "Past" once its departure has gone by, or as soon as it reaches a
    // terminal status — so a departed Confirmed booking reaches the tab that offers the rate
    // action.
    val upcomingBookings = remember(state.bookings, nowMs) {
        state.bookings
            .filter { it.departureTime > nowMs && !it.status.isTerminal }
            .sortedBy { it.departureTime }
    }
    val pastBookings = remember(state.bookings, nowMs) {
        state.bookings
            .filter { it.departureTime <= nowMs || it.status.isTerminal }
            .sortedByDescending { it.departureTime }
    }
    val filteredPastBookings = remember(pastBookings, state.pastSearchQuery) {
        if (state.pastSearchQuery.isBlank()) {
            pastBookings
        } else {
            pastBookings.filter {
                it.originName.contains(state.pastSearchQuery, ignoreCase = true) ||
                    it.destinationName.contains(state.pastSearchQuery, ignoreCase = true)
            }
        }
    }

    // Hosted inside the "Mis viajes" tab, which owns the top bar and the window insets.
    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0)) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Cancel failures, so a failed cancel is distinguishable from a successful one. Tap
            // to dismiss, matching BookingRequestsScreen.
            state.error?.let { error ->
                ErrorMessage(
                    message = stringResource(error.asStringResource()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                        .clickable { onAction(PassengerBookingsAction.OnDismissError) },
                )
            }

            SecondaryTabRow(selectedTabIndex = state.selectedTab.ordinal) {
                Tab(
                    selected = state.selectedTab == PassengerBookingsTab.Upcoming,
                    onClick = { onAction(PassengerBookingsAction.OnTabSelected(PassengerBookingsTab.Upcoming)) },
                    text = { Text(stringResource(Res.string.bookings_tab_upcoming)) }
                )
                Tab(
                    selected = state.selectedTab == PassengerBookingsTab.Past,
                    onClick = { onAction(PassengerBookingsAction.OnTabSelected(PassengerBookingsTab.Past)) },
                    text = { Text(stringResource(Res.string.bookings_tab_past)) }
                )
            }

            if (state.selectedTab == PassengerBookingsTab.Past && pastBookings.isNotEmpty()) {
                OutlinedTextField(
                    value = state.pastSearchQuery,
                    onValueChange = { onAction(PassengerBookingsAction.OnPastSearchQueryChanged(it)) },
                    placeholder = { Text(stringResource(Res.string.bookings_past_search_placeholder)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                )
            }

            val pullRefreshState = rememberPullToRefreshState()
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(PassengerBookingsAction.Refresh) },
                state = pullRefreshState,
                modifier = Modifier.fillMaxSize(),
            ) {
            when {
                state.isLoading -> ListSkeleton(modifier = Modifier.fillMaxSize())
                else -> when (state.selectedTab) {
                    PassengerBookingsTab.Upcoming -> UpcomingContent(
                        bookings = upcomingBookings,
                        nowMs = nowMs,
                        cancellingId = state.cancellingBookingId,
                        driverNames = state.driverNames,
                        vehicleSummaries = state.vehicleSummaries,
                        onAction = onAction
                    )
                    PassengerBookingsTab.Past -> PastContent(
                        bookings = filteredPastBookings,
                        searchActive = state.pastSearchQuery.isNotBlank(),
                        nowMs = nowMs,
                        driverNames = state.driverNames,
                        vehicleSummaries = state.vehicleSummaries,
                        onAction = onAction
                    )
                }
            }
            }
        }
    }

    state.showCancelConfirmFor?.let { bookingId ->
        // Withdrawing a still-pending request and cancelling an already-confirmed seat have
        // different real-world consequences, so they get different copy.
        val isPending = state.bookings.firstOrNull { it.id == bookingId }?.status is BookingStatus.Pending
        ConfirmDialog(
            title = stringResource(
                if (isPending) Res.string.cancel_pending_confirm_title else Res.string.cancel_confirm_title
            ),
            description = stringResource(
                if (isPending) Res.string.cancel_pending_confirm_body else Res.string.cancel_confirm_body
            ),
            confirmText = stringResource(
                if (isPending) Res.string.cancel_pending_confirm_button else Res.string.cancel_confirm_button
            ),
            onConfirm = { onAction(PassengerBookingsAction.OnConfirmCancel(bookingId)) },
            onDismiss = { onAction(PassengerBookingsAction.OnDismissCancelDialog) },
            isDestructive = true
        )
    }
}

@Composable
private fun UpcomingContent(
    bookings: List<Booking>,
    nowMs: Long,
    cancellingId: String?,
    driverNames: Map<String, String>,
    vehicleSummaries: Map<String, String>,
    onAction: (PassengerBookingsAction) -> Unit
) {
    if (bookings.isEmpty()) {
        EmptyState(
            icon = vectorResource(Res.drawable.bookmarks_24px),
            title = stringResource(Res.string.bookings_upcoming_empty_title),
            description = stringResource(Res.string.bookings_upcoming_empty_subtitle),
            primaryAction = ActionButton(
                label = stringResource(Res.string.bookings_search_trips_action),
                onClick = { onAction(PassengerBookingsAction.OnSearchTripsClick) }
            ),
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val grouped = remember(bookings, nowMs) {
        groupByRelativeDate(bookings, nowMs) { it.departureTime }
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = Spacing.lg)
    ) {
        grouped.forEach { (group, items) ->
            stickyHeader(key = group.name) {
                BookingDateGroupHeader(
                    group = group,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(items, key = { it.id }) { booking ->
                val isConfirmed = booking.status is BookingStatus.Confirmed
                EnrichedBookingCard(
                    booking = booking,
                    nowMs = nowMs,
                    driverName = driverNames[booking.driverId],
                    vehicleSummary = vehicleSummaries[booking.tripId],
                    onCancelClick = { onAction(PassengerBookingsAction.OnCancelBookingClick(it)) },
                    onTrackTrip = { tripId -> onAction(PassengerBookingsAction.OnTrackTrip(tripId)) },
                    onMessageDriver = if (isConfirmed) {
                        {
                            onAction(
                                PassengerBookingsAction.OnMessageDriver(
                                    bookingId = booking.id,
                                    tripId = booking.tripId,
                                    driverName = driverNames[booking.driverId] ?: "",
                                    isReadOnly = false
                                )
                            )
                        }
                    } else null,
                    modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.xs)
                )
            }
        }
    }
}

@Composable
private fun PastContent(
    bookings: List<Booking>,
    searchActive: Boolean,
    nowMs: Long,
    driverNames: Map<String, String>,
    vehicleSummaries: Map<String, String>,
    onAction: (PassengerBookingsAction) -> Unit
) {
    if (bookings.isEmpty()) {
        if (searchActive) {
            EmptyState(
                icon = vectorResource(Res.drawable.bookmarks_24px),
                title = stringResource(Res.string.bookings_past_search_no_results),
                description = "",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            EmptyState(
                icon = vectorResource(Res.drawable.bookmarks_24px),
                title = stringResource(Res.string.bookings_past_empty_title),
                description = stringResource(Res.string.bookings_past_empty_subtitle),
                primaryAction = ActionButton(
                    label = stringResource(Res.string.bookings_search_trips_action),
                    onClick = { onAction(PassengerBookingsAction.OnSearchTripsClick) }
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm)
    ) {
        items(bookings, key = { it.id }) { booking ->
            EnrichedBookingCard(
                booking = booking,
                nowMs = nowMs,
                driverName = driverNames[booking.driverId],
                vehicleSummary = vehicleSummaries[booking.tripId],
                onRateBooking = { bookingId, tripId, rateeId, _ ->
                    // EnrichedBookingCard has no driver-name field to draw from (Booking only
                    // denormalizes the passenger's), so the resolved map from the ViewModel wins.
                    val rateeName = driverNames[rateeId] ?: ""
                    onAction(PassengerBookingsAction.OnRateBooking(bookingId, tripId, rateeId, rateeName))
                },
                modifier = Modifier.padding(vertical = Spacing.xs)
            )
        }
    }
}

/** A booking that can no longer become active, whatever its departure time says. */
private val BookingStatus.isTerminal: Boolean
    get() = this is BookingStatus.Cancelled || this is BookingStatus.Rejected

@Preview
@Composable
private fun PassengerBookingsEmptyPreview() {
    CarpoolTheme {
        PassengerBookingsContent(
            state = PassengerBookingsUiState(isLoading = false, bookings = emptyList()),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun PassengerBookingsWithDataPreview() {
    CarpoolTheme {
        PassengerBookingsContent(
            state = PassengerBookingsUiState(
                isLoading = false,
                bookings = listOf(
                    Booking(
                        id = "b1", tripId = "t1", passengerId = "p1", driverId = "d1",
                        passengerName = "Juan Pablo", passengerEmail = "juan@eia.edu.co",
                        originName = "Casa", destinationName = "Universidad EIA",
                        departureTime = Clock.System.now().toEpochMilliseconds() + 3_600_000L,
                        status = BookingStatus.Confirmed,
                        createdAt = Clock.System.now().toEpochMilliseconds()
                    ),
                    Booking(
                        id = "b2", tripId = "t2", passengerId = "p1", driverId = "d1",
                        passengerName = "Juan Pablo", passengerEmail = "juan@eia.edu.co",
                        originName = "EIA", destinationName = "Casa",
                        departureTime = Clock.System.now().toEpochMilliseconds() - 3_600_000L,
                        status = BookingStatus.Cancelled,
                        createdAt = Clock.System.now().toEpochMilliseconds() - 7_200_000L
                    )
                )
            ),
            onAction = {}
        )
    }
}
