package com.juanpablo0612.carpool.presentation.booking.passenger

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.presentation.booking.asStringResource
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.booking.passenger.components.BookingDateGroup
import com.juanpablo0612.carpool.presentation.booking.passenger.components.BookingDateGroupHeader
import com.juanpablo0612.carpool.presentation.booking.passenger.components.EnrichedBookingCard
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.bookings_past_empty_subtitle
import enrutadoseia.composeapp.generated.resources.bookings_past_empty_title
import enrutadoseia.composeapp.generated.resources.bookings_tab_past
import enrutadoseia.composeapp.generated.resources.bookings_tab_upcoming
import enrutadoseia.composeapp.generated.resources.bookings_upcoming_empty_subtitle
import enrutadoseia.composeapp.generated.resources.bookings_upcoming_empty_title
import enrutadoseia.composeapp.generated.resources.bookmarks_24px
import enrutadoseia.composeapp.generated.resources.cancel_confirm_body
import enrutadoseia.composeapp.generated.resources.cancel_confirm_button
import enrutadoseia.composeapp.generated.resources.cancel_confirm_title
import enrutadoseia.composeapp.generated.resources.passenger_bookings_title
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun PassengerBookingsScreen(
    viewModel: PassengerBookingsViewModel,
    onBackClick: () -> Unit,
    onNavigateToTripTracking: (String) -> Unit = {},
    onNavigateToRating: (bookingId: String, tripId: String, rateeId: String, rateeName: String) -> Unit = { _, _, _, _ -> }
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            PassengerBookingsEvent.NavigateBack -> onBackClick()
            is PassengerBookingsEvent.NavigateToTripTracking -> onNavigateToTripTracking(event.tripId)
            is PassengerBookingsEvent.NavigateToRating -> onNavigateToRating(
                event.bookingId, event.tripId, event.rateeId, event.rateeName
            )
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
    val nowMs = remember { Clock.System.now().toEpochMilliseconds() }
    // A booking belongs to "Past" once its departure has gone by, or as soon as it reaches a
    // terminal status. The previous split kept every Confirmed booking in "Upcoming" regardless
    // of date, which meant a departed-and-confirmed booking never reached the tab that offers
    // the rate action — the whole post-trip rating flow was unreachable because of it.
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.passenger_bookings_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // The ViewModel has always recorded cancel failures here; nothing rendered them, so
            // a failed cancel was indistinguishable from a successful one. Tap to dismiss,
            // matching BookingRequestsScreen.
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

            when {
                state.isLoading -> ListSkeleton(modifier = Modifier.fillMaxSize())
                else -> when (state.selectedTab) {
                    PassengerBookingsTab.Upcoming -> UpcomingContent(
                        bookings = upcomingBookings,
                        nowMs = nowMs,
                        cancellingId = state.cancellingBookingId,
                        onAction = onAction
                    )
                    PassengerBookingsTab.Past -> PastContent(
                        bookings = pastBookings,
                        nowMs = nowMs,
                        onAction = onAction
                    )
                }
            }
        }
    }

    state.showCancelConfirmFor?.let { bookingId ->
        ConfirmDialog(
            title = stringResource(Res.string.cancel_confirm_title),
            description = stringResource(Res.string.cancel_confirm_body),
            confirmText = stringResource(Res.string.cancel_confirm_button),
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
    onAction: (PassengerBookingsAction) -> Unit
) {
    if (bookings.isEmpty()) {
        EmptyState(
            icon = vectorResource(Res.drawable.bookmarks_24px),
            title = stringResource(Res.string.bookings_upcoming_empty_title),
            description = stringResource(Res.string.bookings_upcoming_empty_subtitle),
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val grouped = groupByRelativeDate(bookings, nowMs)

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
                EnrichedBookingCard(
                    booking = booking,
                    nowMs = nowMs,
                    onCancelClick = { onAction(PassengerBookingsAction.OnCancelBookingClick(it)) },
                    onTrackTrip = { tripId -> onAction(PassengerBookingsAction.OnTrackTrip(tripId)) },
                    modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.xs)
                )
            }
        }
    }
}

@Composable
private fun PastContent(
    bookings: List<Booking>,
    nowMs: Long,
    onAction: (PassengerBookingsAction) -> Unit
) {
    if (bookings.isEmpty()) {
        EmptyState(
            icon = vectorResource(Res.drawable.bookmarks_24px),
            title = stringResource(Res.string.bookings_past_empty_title),
            description = stringResource(Res.string.bookings_past_empty_subtitle),
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm)
    ) {
        items(bookings, key = { it.id }) { booking ->
            EnrichedBookingCard(
                booking = booking,
                nowMs = nowMs,
                onRateBooking = { bookingId, tripId, rateeId, rateeName ->
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

private fun groupByRelativeDate(
    bookings: List<Booking>,
    nowMs: Long
): List<Pair<BookingDateGroup, List<Booking>>> {
    val tz = TimeZone.currentSystemDefault()
    val nowDate = kotlin.time.Instant.fromEpochMilliseconds(nowMs).toLocalDateTime(tz).date
    val tomorrow = nowDate.plus(1, DateTimeUnit.DAY)
    val nextWeek = nowDate.plus(7, DateTimeUnit.DAY)

    val groups = LinkedHashMap<BookingDateGroup, MutableList<Booking>>()
    bookings.forEach { booking ->
        val date = kotlin.time.Instant.fromEpochMilliseconds(booking.departureTime)
            .toLocalDateTime(tz).date
        val group = when {
            date == nowDate -> BookingDateGroup.TODAY
            date == tomorrow -> BookingDateGroup.TOMORROW
            date < nextWeek -> BookingDateGroup.THIS_WEEK
            else -> BookingDateGroup.LATER
        }
        groups.getOrPut(group) { mutableListOf() }.add(booking)
    }
    return groups.entries.map { (k, v) -> k to v }
}

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
