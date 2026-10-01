package com.juanpablo0612.carpool.presentation.mytrips

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.booking.components.PendingRequestsBanner
import com.juanpablo0612.carpool.presentation.mytrips.components.MyTripCard
import com.juanpablo0612.carpool.presentation.rating.RatingTarget
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.CenteredContent
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.centeredContent
import com.juanpablo0612.carpool.presentation.ui.util.departureDayLabel
import com.juanpablo0612.carpool.presentation.ui.util.plusHorizontal
import com.juanpablo0612.carpool.presentation.ui.util.rememberNowMs
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.calendar_month_24px
import enrutadoseia.composeapp.generated.resources.cancel_confirm_body
import enrutadoseia.composeapp.generated.resources.cancel_confirm_button
import enrutadoseia.composeapp.generated.resources.cancel_confirm_title
import enrutadoseia.composeapp.generated.resources.cancel_pending_confirm_body
import enrutadoseia.composeapp.generated.resources.cancel_pending_confirm_button
import enrutadoseia.composeapp.generated.resources.cancel_pending_confirm_title
import enrutadoseia.composeapp.generated.resources.home_action_publish_trip
import enrutadoseia.composeapp.generated.resources.my_trips_empty_history_body
import enrutadoseia.composeapp.generated.resources.my_trips_empty_history_title
import enrutadoseia.composeapp.generated.resources.my_trips_empty_upcoming_body
import enrutadoseia.composeapp.generated.resources.my_trips_empty_upcoming_title
import enrutadoseia.composeapp.generated.resources.my_trips_filter_all
import enrutadoseia.composeapp.generated.resources.my_trips_filter_driving
import enrutadoseia.composeapp.generated.resources.my_trips_filter_riding
import enrutadoseia.composeapp.generated.resources.my_trips_segment_history
import enrutadoseia.composeapp.generated.resources.my_trips_segment_upcoming
import enrutadoseia.composeapp.generated.resources.nav_my_trips
import enrutadoseia.composeapp.generated.resources.passenger_home_title
import enrutadoseia.composeapp.generated.resources.trip_cancel_confirm_body
import enrutadoseia.composeapp.generated.resources.trip_cancel_confirm_button
import enrutadoseia.composeapp.generated.resources.trip_cancel_confirm_title
import enrutadoseia.composeapp.generated.resources.trip_start_confirm_body
import enrutadoseia.composeapp.generated.resources.trip_start_confirm_button
import enrutadoseia.composeapp.generated.resources.trip_start_confirm_title
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.Instant

@Composable
fun MyTripsScreen(
    viewModel: MyTripsViewModel,
    onOpenTripDetail: (String) -> Unit,
    onOpenPassengers: (String) -> Unit,
    onOpenTracking: (String) -> Unit,
    onOpenRequests: () -> Unit,
    onSearchTrips: () -> Unit,
    onPublishTrip: () -> Unit,
    onOpenChat: (bookingId: String, tripId: String, otherPartyName: String, isReadOnly: Boolean) -> Unit,
    onRateDriver: (RatingTarget) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is MyTripsEvent.NavigateToTripDetail -> onOpenTripDetail(event.tripId)
            is MyTripsEvent.NavigateToPassengers -> onOpenPassengers(event.tripId)
            is MyTripsEvent.NavigateToTracking -> onOpenTracking(event.tripId)
            MyTripsEvent.NavigateToRequests -> onOpenRequests()
            MyTripsEvent.NavigateToSearch -> onSearchTrips()
            MyTripsEvent.NavigateToPublish -> onPublishTrip()
            is MyTripsEvent.NavigateToChat -> onOpenChat(event.bookingId, event.tripId, event.otherPartyName, event.isReadOnly)
            is MyTripsEvent.NavigateToRating -> onRateDriver(event.target)
        }
    }

    MyTripsContent(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun MyTripsContent(
    state: MyTripsUiState,
    onAction: (MyTripsAction) -> Unit,
    now: Long = rememberNowMs(),
) {
    state.confirmation?.let { ConfirmationDialog(it, onAction) }

    Scaffold(contentWindowInsets = ScreenInsets, containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            MyTripsHeader(segment = state.segment, filter = state.filter, onAction = onAction)
            when {
                state.isLoading -> ListSkeleton(modifier = Modifier.fillMaxSize().padding(top = Spacing.lg))
                state.error == MyTripsError.LoadFailed -> ErrorState(
                    description = stringResource(state.error.asStringResource()),
                    onRetry = { onAction(MyTripsAction.OnRetry) },
                    modifier = Modifier.fillMaxSize(),
                )
                else -> MyTripsList(state = state, now = now, onAction = onAction)
            }
        }
    }
}

@Composable
private fun MyTripsHeader(segment: MyTripsSegment, filter: MyTripsFilter, onAction: (MyTripsAction) -> Unit) {
    Column(
        modifier = Modifier
            .centeredContent(ContentWidth.list)
            .padding(start = Spacing.screenHorizontal, end = Spacing.screenHorizontal, top = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.nav_my_trips),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() },
        )
        val segments = listOf(
            MyTripsSegment.Upcoming to Res.string.my_trips_segment_upcoming,
            MyTripsSegment.History to Res.string.my_trips_segment_history,
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            segments.forEachIndexed { index, (value, label) ->
                SegmentedButton(
                    selected = segment == value,
                    onClick = { onAction(MyTripsAction.OnSegmentSelected(value)) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = segments.size),
                    icon = {},
                ) { Text(stringResource(label), style = MaterialTheme.typography.titleSmall) }
            }
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            listOf(
                MyTripsFilter.All to Res.string.my_trips_filter_all,
                MyTripsFilter.Driving to Res.string.my_trips_filter_driving,
                MyTripsFilter.Riding to Res.string.my_trips_filter_riding,
            ).forEach { (value, label) ->
                FilterChip(
                    selected = filter == value,
                    onClick = { onAction(MyTripsAction.OnFilterSelected(value)) },
                    label = { Text(stringResource(label)) },
                )
            }
        }
    }
}

@Composable
private fun MyTripsList(state: MyTripsUiState, now: Long, onAction: (MyTripsAction) -> Unit) {
    val visible = remember(state.items, state.segment, state.filter, now) {
        state.items.select(state.segment, state.filter, now)
    }
    val groups = remember(visible) { visible.groupBy { it.localDate() } }
    val isUpcoming = state.segment == MyTripsSegment.Upcoming
    val showBanner = isUpcoming && state.pendingRequestCount > 0 && state.filter != MyTripsFilter.Riding

    if (visible.isEmpty() && !showBanner) {
        MyTripsEmptyState(isUpcoming = isUpcoming, onAction = onAction)
        return
    }
    CenteredContent(ContentWidth.list, modifier = Modifier.fillMaxSize(), gutter = 0.dp) { margin ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = Spacing.screenHorizontal, vertical = Spacing.lg).plusHorizontal(margin),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            if (state.error == MyTripsError.ActionFailed) {
                item(key = "error") {
                    ErrorMessage(message = stringResource(state.error.asStringResource()))
                }
            }
            if (showBanner) {
                item(key = "requests") {
                    PendingRequestsBanner(count = state.pendingRequestCount, onClick = { onAction(MyTripsAction.OnOpenRequests) })
                }
            }
            groups.forEach { (_, itemsOfDay) ->
                item(key = "day_${itemsOfDay.first().key}") {
                    Text(
                        text = departureDayLabel(itemsOfDay.first().departureTime, now).replaceFirstChar { it.uppercaseChar() },
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(top = Spacing.sm)
                            .semantics { heading() },
                    )
                }
                items(itemsOfDay, key = { it.key }) { item ->
                    MyTripCard(
                        item = item,
                        isUpcoming = isUpcoming,
                        isBusy = state.busyKey == item.key,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@Composable
private fun MyTripsEmptyState(isUpcoming: Boolean, onAction: (MyTripsAction) -> Unit) {
    if (isUpcoming) {
        EmptyState(
            icon = vectorResource(Res.drawable.calendar_month_24px),
            title = stringResource(Res.string.my_trips_empty_upcoming_title),
            description = stringResource(Res.string.my_trips_empty_upcoming_body),
            primaryAction = ActionButton(stringResource(Res.string.passenger_home_title)) { onAction(MyTripsAction.OnSearchTrips) },
            secondaryAction = ActionButton(stringResource(Res.string.home_action_publish_trip)) { onAction(MyTripsAction.OnPublishTrip) },
            modifier = Modifier.fillMaxSize(),
        )
    } else {
        EmptyState(
            icon = vectorResource(Res.drawable.calendar_month_24px),
            title = stringResource(Res.string.my_trips_empty_history_title),
            description = stringResource(Res.string.my_trips_empty_history_body),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun ConfirmationDialog(confirmation: MyTripsConfirmation, onAction: (MyTripsAction) -> Unit) {
    val (title, body, button) = when (confirmation) {
        is MyTripsConfirmation.StartTrip -> Triple(
            Res.string.trip_start_confirm_title, Res.string.trip_start_confirm_body, Res.string.trip_start_confirm_button,
        )
        is MyTripsConfirmation.CancelTrip -> Triple(
            Res.string.trip_cancel_confirm_title, Res.string.trip_cancel_confirm_body, Res.string.trip_cancel_confirm_button,
        )
        is MyTripsConfirmation.CancelBooking -> if (confirmation.isPending) {
            Triple(Res.string.cancel_pending_confirm_title, Res.string.cancel_pending_confirm_body, Res.string.cancel_pending_confirm_button)
        } else {
            Triple(Res.string.cancel_confirm_title, Res.string.cancel_confirm_body, Res.string.cancel_confirm_button)
        }
    }
    ConfirmDialog(
        title = stringResource(title),
        description = stringResource(body),
        confirmText = stringResource(button),
        onConfirm = { onAction(MyTripsAction.OnConfirm) },
        onDismiss = { onAction(MyTripsAction.OnDismissConfirmation) },
        // Starting a trip is a normal step; cancelling one or a seat affects other people.
        isDestructive = confirmation !is MyTripsConfirmation.StartTrip,
    )
}

private fun MyTripItem.localDate(): LocalDate =
    Instant.fromEpochMilliseconds(departureTime).toLocalDateTime(TimeZone.currentSystemDefault()).date

@Preview
@Composable
private fun MyTripsEmptyPreview() {
    CarpoolTheme {
        MyTripsContent(state = MyTripsUiState(isLoading = false), onAction = {})
    }
}
