package com.juanpablo0612.carpool.presentation.trip.driver_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.TripStatusBadge
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.utils.formatLongDate
import com.juanpablo0612.carpool.presentation.utils.formatShortTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.driver_trips_past_empty_subtitle
import enrutadoseia.composeapp.generated.resources.driver_trips_past_empty_title
import enrutadoseia.composeapp.generated.resources.driver_trips_upcoming_empty_subtitle
import enrutadoseia.composeapp.generated.resources.driver_trips_upcoming_empty_title
import enrutadoseia.composeapp.generated.resources.nav_my_trips
import enrutadoseia.composeapp.generated.resources.publish_trip_fab
import enrutadoseia.composeapp.generated.resources.relative_date_later
import enrutadoseia.composeapp.generated.resources.relative_date_this_week
import enrutadoseia.composeapp.generated.resources.relative_date_today
import enrutadoseia.composeapp.generated.resources.relative_date_tomorrow
import enrutadoseia.composeapp.generated.resources.tab_past
import enrutadoseia.composeapp.generated.resources.tab_upcoming
import enrutadoseia.composeapp.generated.resources.trip_action_finish
import enrutadoseia.composeapp.generated.resources.trip_action_start
import enrutadoseia.composeapp.generated.resources.trip_action_view_passengers
import enrutadoseia.composeapp.generated.resources.trip_cancel_confirm_body
import enrutadoseia.composeapp.generated.resources.trip_cancel_confirm_button
import enrutadoseia.composeapp.generated.resources.trip_cancel_confirm_title
import enrutadoseia.composeapp.generated.resources.trip_seats_occupied
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun DriverTripsScreen(
    viewModel: DriverTripsViewModel,
    onBackClick: () -> Unit,
    onNavigateToRoutesList: () -> Unit,
    onNavigateToTripDetail: (String) -> Unit,
    onNavigateToPassengers: (String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            DriverTripsEvent.NavigateBack -> onBackClick()
            DriverTripsEvent.NavigateToRoutesList -> onNavigateToRoutesList()
            is DriverTripsEvent.NavigateToTripDetail -> onNavigateToTripDetail(event.tripId)
            is DriverTripsEvent.NavigateToPassengers -> onNavigateToPassengers(event.tripId)
        }
    }

    DriverTripsContent(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverTripsContent(
    state: DriverTripsUiState,
    onAction: (DriverTripsAction) -> Unit
) {
    val isUpcoming = state.tab is TripsTab.Upcoming

    state.pendingCancelTripId?.let { tripId ->
        ConfirmDialog(
            title = stringResource(Res.string.trip_cancel_confirm_title),
            description = stringResource(Res.string.trip_cancel_confirm_body),
            confirmText = stringResource(Res.string.trip_cancel_confirm_button),
            onConfirm = { onAction(DriverTripsAction.ConfirmCancel(tripId)) },
            onDismiss = { onAction(DriverTripsAction.DismissCancel) },
            isDestructive = true
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(Res.string.nav_my_trips)) })
        },
        floatingActionButton = {
            if (isUpcoming) {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(Res.string.publish_trip_fab)) },
                    icon = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.add_24px),
                            contentDescription = null
                        )
                    },
                    onClick = { onAction(DriverTripsAction.PublishTrip) }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            SecondaryTabRow(selectedTabIndex = if (isUpcoming) 0 else 1) {
                Tab(
                    selected = isUpcoming,
                    onClick = { onAction(DriverTripsAction.SelectTab(TripsTab.Upcoming)) },
                    text = { Text(stringResource(Res.string.tab_upcoming)) }
                )
                Tab(
                    selected = !isUpcoming,
                    onClick = { onAction(DriverTripsAction.SelectTab(TripsTab.Past)) },
                    text = { Text(stringResource(Res.string.tab_past)) }
                )
            }

            when {
                state.isLoading -> ListSkeleton(
                    modifier = Modifier.fillMaxSize()
                )
                state.trips.isEmpty() -> {
                    if (isUpcoming) {
                        EmptyState(
                            icon = vectorResource(Res.drawable.directions_car_24px),
                            title = stringResource(Res.string.driver_trips_upcoming_empty_title),
                            description = stringResource(Res.string.driver_trips_upcoming_empty_subtitle),
                            primaryAction = ActionButton(
                                label = stringResource(Res.string.publish_trip_fab),
                                onClick = { onAction(DriverTripsAction.PublishTrip) }
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        EmptyState(
                            icon = vectorResource(Res.drawable.directions_car_24px),
                            title = stringResource(Res.string.driver_trips_past_empty_title),
                            description = stringResource(Res.string.driver_trips_past_empty_subtitle),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                else -> {
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val grouped = if (isUpcoming) {
                        state.trips.groupBy { ts ->
                            val date = Instant.fromEpochMilliseconds(ts.trip.departureTime)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                            dateGroupKey(date, today)
                        }
                    } else {
                        null
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = Spacing.lg,
                            end = Spacing.lg,
                            top = Spacing.md,
                            bottom = 88.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        if (grouped != null) {
                            grouped.forEach { (key, tripsInGroup) ->
                                stickyHeader(key = key) {
                                    val label = dateGroupLabel(key)
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = Spacing.sm)
                                    )
                                }
                                items(tripsInGroup, key = { it.trip.id }) { ts ->
                                    DriverTripCard(
                                        tripWithStats = ts,
                                        onStartTrip = { onAction(DriverTripsAction.StartTrip(ts.trip.id)) },
                                        onFinishTrip = { onAction(DriverTripsAction.FinishTrip(ts.trip.id)) },
                                        onCancelTrip = { onAction(DriverTripsAction.CancelTrip(ts.trip.id)) },
                                        onViewPassengers = { onAction(DriverTripsAction.OpenPassengers(ts.trip.id)) },
                                        onCardClick = { onAction(DriverTripsAction.OpenTrip(ts.trip.id)) }
                                    )
                                }
                            }
                        } else {
                            items(state.trips, key = { it.trip.id }) { ts ->
                                DriverTripCard(
                                    tripWithStats = ts,
                                    onStartTrip = { onAction(DriverTripsAction.StartTrip(ts.trip.id)) },
                                    onFinishTrip = { onAction(DriverTripsAction.FinishTrip(ts.trip.id)) },
                                    onCancelTrip = { onAction(DriverTripsAction.CancelTrip(ts.trip.id)) },
                                    onViewPassengers = { onAction(DriverTripsAction.OpenPassengers(ts.trip.id)) },
                                    onCardClick = { onAction(DriverTripsAction.OpenTrip(ts.trip.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DriverTripCard(
    tripWithStats: TripWithStats,
    onStartTrip: () -> Unit,
    onFinishTrip: () -> Unit,
    onCancelTrip: () -> Unit,
    onViewPassengers: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trip = tripWithStats.trip
    val local = Instant.fromEpochMilliseconds(trip.departureTime)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val timeStr = formatShortTime(local.hour, local.minute)
    val dateStr = formatLongDate(local.year, local.monthNumber, local.dayOfMonth)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$dateStr · $timeStr",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
                TripStatusBadge(status = trip.status)
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = "${trip.origin.name} → ${trip.destination.name}",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = stringResource(
                    Res.string.trip_seats_occupied,
                    tripWithStats.occupiedSeats,
                    trip.seatCount
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            tripWithStats.vehicle?.let { v ->
                Text(
                    text = "${v.brand} ${v.model} · ${v.licensePlate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onViewPassengers) {
                    Text(stringResource(Res.string.trip_action_view_passengers))
                }

                when (trip.status) {
                    TripStatus.Active -> Button(
                        onClick = onStartTrip,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.trip_action_start),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    TripStatus.InProgress -> Button(
                        onClick = onFinishTrip,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.trip_action_finish),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    TripStatus.Completed, TripStatus.Cancelled -> Unit
                }
            }
        }
    }
}

private enum class DateGroupKey { Today, Tomorrow, ThisWeek, Later }

private fun dateGroupKey(date: LocalDate, today: LocalDate): DateGroupKey {
    val tomorrow = today.plus(1, DateTimeUnit.DAY)
    val endOfWeek = today.plus(7, DateTimeUnit.DAY)
    return when {
        date == today -> DateGroupKey.Today
        date == tomorrow -> DateGroupKey.Tomorrow
        date < endOfWeek -> DateGroupKey.ThisWeek
        else -> DateGroupKey.Later
    }
}

@Composable
private fun dateGroupLabel(key: DateGroupKey): String = when (key) {
    DateGroupKey.Today -> stringResource(Res.string.relative_date_today)
    DateGroupKey.Tomorrow -> stringResource(Res.string.relative_date_tomorrow)
    DateGroupKey.ThisWeek -> stringResource(Res.string.relative_date_this_week)
    DateGroupKey.Later -> stringResource(Res.string.relative_date_later)
}

@Preview
@Composable
private fun DriverTripsEmptyPreview() {
    CarpoolTheme {
        DriverTripsContent(
            state = DriverTripsUiState(isLoading = false, trips = emptyList()),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun DriverTripsWithDataPreview() {
    val origin = Place(name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6)
    val vehicle = Vehicle(
        id = "v1", driverId = "d1", brand = "Toyota", model = "Corolla",
        licensePlate = "ABC123", color = "Blanco", year = 2020, seatsAvailable = 3
    )
    CarpoolTheme {
        DriverTripsContent(
            state = DriverTripsUiState(
                isLoading = false,
                trips = listOf(
                    TripWithStats(
                        trip = Trip(
                            id = "1", routeId = "r1", driverId = "d1", vehicleId = "v1",
                            origin = origin,
                            destination = Place.UNIVERSITY_EIA,
                            waypoints = emptyList(),
                            departureTime = Clock.System.now().toEpochMilliseconds() + 3_600_000L,
                            seatCount = 3,
                            status = TripStatus.Active
                        ),
                        occupiedSeats = 1,
                        vehicle = vehicle
                    ),
                    TripWithStats(
                        trip = Trip(
                            id = "2", routeId = "r1", driverId = "d1", vehicleId = "v1",
                            origin = origin,
                            destination = Place.UNIVERSITY_EIA,
                            waypoints = emptyList(),
                            departureTime = Clock.System.now().toEpochMilliseconds() + 7_200_000L,
                            seatCount = 3,
                            status = TripStatus.InProgress
                        ),
                        occupiedSeats = 2,
                        vehicle = vehicle
                    )
                )
            ),
            onAction = {}
        )
    }
}
