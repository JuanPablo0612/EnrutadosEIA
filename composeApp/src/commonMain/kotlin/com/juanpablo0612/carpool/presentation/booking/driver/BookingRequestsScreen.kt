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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.booking.asStringResource
import com.juanpablo0612.carpool.presentation.booking.driver.components.BookingDecisionDialogs
import com.juanpablo0612.carpool.presentation.booking.driver.components.BookingRequestCard
import com.juanpablo0612.carpool.presentation.booking.driver.components.PassengerCard
import com.juanpablo0612.carpool.presentation.booking.driver.components.TripBookingsHeader
import com.juanpablo0612.carpool.presentation.booking.driver.decision.BookingDecisionAction
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.rememberNowMs
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.action_dismiss
import enrutadoseia.composeapp.generated.resources.booking_requests_tab_confirmed
import enrutadoseia.composeapp.generated.resources.booking_requests_tab_pending
import enrutadoseia.composeapp.generated.resources.booking_requests_title
import enrutadoseia.composeapp.generated.resources.confirmed_empty_subtitle
import enrutadoseia.composeapp.generated.resources.confirmed_empty_title
import enrutadoseia.composeapp.generated.resources.inbox_24px
import enrutadoseia.composeapp.generated.resources.label_pair
import enrutadoseia.composeapp.generated.resources.pending_empty_subtitle
import enrutadoseia.composeapp.generated.resources.pending_empty_title
import enrutadoseia.composeapp.generated.resources.person_24px
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BookingRequestsScreen(
    viewModel: BookingRequestsViewModel,
    onNavigateToPassengerProfile: (String) -> Unit,
    onNavigateToTripPassengers: (String) -> Unit,
    onNavigateToChat: (bookingId: String, tripId: String, otherPartyName: String, isReadOnly: Boolean) -> Unit,
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BookingRequestsEvent.NavigateToPassengerProfile -> onNavigateToPassengerProfile(event.passengerId)
            is BookingRequestsEvent.NavigateToTripPassengers -> onNavigateToTripPassengers(event.tripId)
            // Only upcoming trips are listed here, so the chat is always writable.
            is BookingRequestsEvent.NavigateToChat ->
                onNavigateToChat(event.bookingId, event.tripId, event.passengerName, false)
        }
    }

    BookingRequestsContent(state = state, onAction = viewModel::onAction, onBackClick = onBackClick)
}

@Composable
fun BookingRequestsContent(
    state: BookingRequestsUiState,
    onAction: (BookingRequestsAction) -> Unit,
    onBackClick: () -> Unit,
    nowMs: Long = rememberNowMs(),
) {
    val onDecision: (BookingDecisionAction) -> Unit = { onAction(BookingRequestsAction.OnDecision(it)) }
    BookingDecisionDialogs(state = state.decisions, onDecision = onDecision)

    Scaffold(
        contentWindowInsets = ScreenInsets,
        topBar = { CarpoolBackTopBar(title = stringResource(Res.string.booking_requests_title), onBack = onBackClick) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            RequestTabs(state = state, onAction = onAction)
            state.decisions.error?.let { error ->
                val dismissLabel = stringResource(Res.string.action_dismiss)
                ErrorMessage(
                    message = stringResource(error.asStringResource()),
                    modifier = Modifier
                        .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
                        .clickable(onClickLabel = dismissLabel) { onDecision(BookingDecisionAction.DismissError) },
                )
            }
            val groups = when (state.tab) {
                BookingRequestsTab.Pending -> state.pending
                BookingRequestsTab.Accepted -> state.accepted
            }
            when {
                state.isLoading -> ListSkeleton(modifier = Modifier.fillMaxSize())
                state.loadError != null -> ErrorState(
                    description = stringResource(state.loadError.asStringResource()),
                    onRetry = { onAction(BookingRequestsAction.OnRetry) },
                    modifier = Modifier.fillMaxSize(),
                )
                groups.isEmpty() -> RequestsEmptyState(tab = state.tab)
                else -> RequestGroups(state = state, groups = groups, nowMs = nowMs, onAction = onAction)
            }
        }
    }
}

@Composable
private fun RequestTabs(state: BookingRequestsUiState, onAction: (BookingRequestsAction) -> Unit) {
    val tabs = listOf(
        Triple(BookingRequestsTab.Pending, Res.string.booking_requests_tab_pending, state.pendingCount),
        Triple(BookingRequestsTab.Accepted, Res.string.booking_requests_tab_confirmed, state.acceptedCount),
    )
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
    ) {
        tabs.forEachIndexed { index, (tab, label, count) ->
            SegmentedButton(
                selected = state.tab == tab,
                onClick = { onAction(BookingRequestsAction.OnTabSelected(tab)) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                icon = {},
            ) {
                Text(text = tabLabel(label, count, isLoading = state.isLoading), style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

/** "Por responder · 2": the count only once it's known and worth showing. */
@Composable
private fun tabLabel(label: StringResource, count: Int, isLoading: Boolean): String {
    val text = stringResource(label)
    return if (isLoading || count == 0) text else stringResource(Res.string.label_pair, text, count.toString())
}

@Composable
private fun RequestGroups(
    state: BookingRequestsUiState,
    groups: List<TripBookings>,
    nowMs: Long,
    onAction: (BookingRequestsAction) -> Unit,
) {
    val onDecision: (BookingDecisionAction) -> Unit = { onAction(BookingRequestsAction.OnDecision(it)) }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = Spacing.screenHorizontal, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        groups.forEach { group ->
            item(key = "trip_${group.tripId}") {
                TripBookingsHeader(
                    group = group,
                    nowMs = nowMs,
                    onClick = { onAction(BookingRequestsAction.OnTripClick(group.tripId)) },
                    modifier = Modifier.padding(top = Spacing.sm),
                )
            }
            items(group.bookings, key = { it.id }) { booking ->
                val isBusy = booking.id in state.decisions.busyIds
                val onViewProfile = { onAction(BookingRequestsAction.OnViewProfile(booking.passengerId)) }
                when (state.tab) {
                    BookingRequestsTab.Pending -> BookingRequestCard(
                        booking = booking,
                        isTripFull = group.isFull,
                        isLastSeatContested = group.isLastSeatContested,
                        isBusy = isBusy,
                        nowMs = nowMs,
                        onDecision = onDecision,
                        onViewProfile = onViewProfile,
                    )
                    BookingRequestsTab.Accepted -> PassengerCard(
                        booking = booking,
                        isBusy = isBusy,
                        canCancel = true,
                        onMessage = { onAction(BookingRequestsAction.OnMessagePassenger(booking)) },
                        onRate = null,
                        onDecision = onDecision,
                        onViewProfile = onViewProfile,
                    )
                }
            }
        }
    }
}

@Composable
private fun RequestsEmptyState(tab: BookingRequestsTab) {
    when (tab) {
        BookingRequestsTab.Pending -> EmptyState(
            icon = vectorResource(Res.drawable.inbox_24px),
            title = stringResource(Res.string.pending_empty_title),
            description = stringResource(Res.string.pending_empty_subtitle),
            modifier = Modifier.fillMaxSize(),
        )
        BookingRequestsTab.Accepted -> EmptyState(
            icon = vectorResource(Res.drawable.person_24px),
            title = stringResource(Res.string.confirmed_empty_title),
            description = stringResource(Res.string.confirmed_empty_subtitle),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview
@Composable
private fun BookingRequestsEmptyPreview() {
    CarpoolTheme {
        BookingRequestsContent(state = BookingRequestsUiState(isLoading = false), onAction = {}, onBackClick = {})
    }
}
