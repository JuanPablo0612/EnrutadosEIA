package com.juanpablo0612.carpool.presentation.booking.driver

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.presentation.booking.asStringResource
import com.juanpablo0612.carpool.presentation.booking.driver.components.BookingDecisionDialogs
import com.juanpablo0612.carpool.presentation.booking.driver.components.BookingRequestCard
import com.juanpablo0612.carpool.presentation.booking.driver.components.PassengerCard
import com.juanpablo0612.carpool.presentation.booking.driver.decision.BookingDecisionAction
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.RouteLineRow
import com.juanpablo0612.carpool.presentation.ui.components.SectionHeader
import com.juanpablo0612.carpool.presentation.ui.components.TripStatusBadge
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.departureDayLabel
import com.juanpablo0612.carpool.presentation.ui.util.formatTime
import com.juanpablo0612.carpool.presentation.ui.util.rememberNowMs
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.action_dismiss
import enrutadoseia.composeapp.generated.resources.label_pair
import enrutadoseia.composeapp.generated.resources.person_24px
import enrutadoseia.composeapp.generated.resources.relative_day_at_time
import enrutadoseia.composeapp.generated.resources.trip_passengers_empty_subtitle
import enrutadoseia.composeapp.generated.resources.trip_passengers_empty_title
import enrutadoseia.composeapp.generated.resources.trip_passengers_section_confirmed
import enrutadoseia.composeapp.generated.resources.trip_passengers_section_pending
import enrutadoseia.composeapp.generated.resources.trip_passengers_title
import enrutadoseia.composeapp.generated.resources.trip_seats_occupied
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun TripPassengersScreen(
    viewModel: TripPassengersViewModel,
    onBackClick: () -> Unit,
    onNavigateToPassengerProfile: (String) -> Unit,
    onNavigateToRating: (bookingId: String, tripId: String, rateeId: String, rateeName: String) -> Unit,
    onNavigateToChat: (bookingId: String, tripId: String, otherPartyName: String, isReadOnly: Boolean) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is TripPassengersEvent.NavigateToPassengerProfile -> onNavigateToPassengerProfile(event.passengerId)
            is TripPassengersEvent.NavigateToChat ->
                onNavigateToChat(event.bookingId, event.tripId, event.passengerName, event.isReadOnly)
            is TripPassengersEvent.NavigateToRating ->
                onNavigateToRating(event.bookingId, event.tripId, event.rateeId, event.rateeName)
        }
    }

    TripPassengersContent(state = state, onAction = viewModel::onAction, onBackClick = onBackClick)
}

@Composable
fun TripPassengersContent(
    state: TripPassengersUiState,
    onAction: (TripPassengersAction) -> Unit,
    onBackClick: () -> Unit,
    nowMs: Long = rememberNowMs(),
) {
    val onDecision: (BookingDecisionAction) -> Unit = { onAction(TripPassengersAction.OnDecision(it)) }
    BookingDecisionDialogs(state = state.decisions, onDecision = onDecision)

    Scaffold(
        topBar = { CarpoolBackTopBar(title = stringResource(Res.string.trip_passengers_title), onBack = onBackClick) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        val modifier = Modifier.fillMaxSize().padding(padding)
        val trip = state.trip
        when {
            state.isLoading -> ListSkeleton(modifier = modifier)
            state.loadError != null -> ErrorState(
                description = stringResource(state.loadError.asStringResource()),
                onRetry = { onAction(TripPassengersAction.OnRetry) },
                modifier = modifier,
            )
            trip != null -> PassengerList(state = state, trip = trip, nowMs = nowMs, onAction = onAction, modifier = modifier)
        }
    }
}

@Composable
private fun PassengerList(
    state: TripPassengersUiState,
    trip: Trip,
    nowMs: Long,
    onAction: (TripPassengersAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onDecision: (BookingDecisionAction) -> Unit = { onAction(TripPassengersAction.OnDecision(it)) }
    // Late requests on a trip that already left can't be answered any more, so they aren't listed.
    val pending = if (state.isOpen) state.pending else emptyList()
    val freeSeats = state.freeSeats
    val sectionPadding = PaddingValues(top = Spacing.sm)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Spacing.screenHorizontal, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item(key = "trip") { TripSummaryCard(trip = trip, nowMs = nowMs) }

        state.decisions.error?.let { error ->
            item(key = "error") {
                val dismissLabel = stringResource(Res.string.action_dismiss)
                ErrorMessage(
                    message = stringResource(error.asStringResource()),
                    modifier = Modifier.clickable(onClickLabel = dismissLabel) {
                        onDecision(BookingDecisionAction.DismissError)
                    },
                )
            }
        }

        if (pending.isEmpty() && state.confirmed.isEmpty()) {
            item(key = "empty") {
                EmptyState(
                    icon = vectorResource(Res.drawable.person_24px),
                    title = stringResource(Res.string.trip_passengers_empty_title),
                    description = stringResource(Res.string.trip_passengers_empty_subtitle),
                )
            }
        }

        if (pending.isNotEmpty()) {
            item(key = "pending_header") {
                SectionHeader(
                    title = sectionTitle(stringResource(Res.string.trip_passengers_section_pending), pending.size),
                    contentPadding = sectionPadding,
                )
            }
            items(pending, key = { it.id }) { booking ->
                BookingRequestCard(
                    booking = booking,
                    isTripFull = freeSeats == 0,
                    isLastSeatContested = freeSeats == 1 && pending.size > 1,
                    isBusy = booking.id in state.decisions.busyIds,
                    nowMs = nowMs,
                    onDecision = onDecision,
                    onViewProfile = { onAction(TripPassengersAction.OnViewProfile(booking.passengerId)) },
                )
            }
        }

        if (state.confirmed.isNotEmpty()) {
            item(key = "confirmed_header") {
                SectionHeader(
                    title = sectionTitle(stringResource(Res.string.trip_passengers_section_confirmed), state.confirmed.size),
                    contentPadding = sectionPadding,
                )
            }
            items(state.confirmed, key = { it.id }) { booking ->
                PassengerCard(
                    booking = booking,
                    isBusy = booking.id in state.decisions.busyIds,
                    canCancel = state.isOpen,
                    onMessage = { onAction(TripPassengersAction.OnMessagePassenger(booking)) },
                    onRate = if (trip.status == TripStatus.Completed) {
                        { onAction(TripPassengersAction.OnRatePassenger(booking)) }
                    } else {
                        null
                    },
                    onDecision = onDecision,
                    onViewProfile = { onAction(TripPassengersAction.OnViewProfile(booking.passengerId)) },
                )
            }
        }
    }
}

/** When the trip leaves, its route and how full it is: the context for every answer below. */
@Composable
private fun TripSummaryCard(trip: Trip, nowMs: Long) {
    CarpoolListCard {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(
                        Res.string.relative_day_at_time,
                        departureDayLabel(trip.departureTime, nowMs).replaceFirstChar { it.uppercaseChar() },
                        formatTime(trip.departureTime),
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                if (trip.status != TripStatus.Active) TripStatusBadge(status = trip.status)
            }
            RouteLineRow(origin = trip.origin.name, destination = trip.destination.name)
            Text(
                text = stringResource(Res.string.trip_seats_occupied, trip.confirmedSeats, trip.seatCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun sectionTitle(title: String, count: Int): String =
    stringResource(Res.string.label_pair, title, count.toString())

@Preview
@Composable
private fun TripPassengersLoadingPreview() {
    CarpoolTheme {
        TripPassengersContent(state = TripPassengersUiState(), onAction = {}, onBackClick = {})
    }
}
