package com.juanpablo0612.carpool.presentation.trip.passengerdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.rating.model.RatingSummary
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripClosedReason
import com.juanpablo0612.carpool.domain.trip.model.TripDriver
import com.juanpablo0612.carpool.domain.trip.model.TripMeetingStop
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.model.TripVehicle
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.BookingBar
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.ConfirmRequestSheet
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.DriverCard
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.DriverMessageCard
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.TripClosedBanner
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.TripDetailHeader
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.TripRouteCard
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.components.TripStats
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.DetailSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.CenteredContent
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.plusHorizontal
import com.juanpablo0612.carpool.presentation.ui.util.rememberNotificationPermissionState
import com.juanpablo0612.carpool.presentation.ui.util.rememberNowMs
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cd_back
import enrutadoseia.composeapp.generated.resources.error_24px
import enrutadoseia.composeapp.generated.resources.route_detail_passenger_title
import enrutadoseia.composeapp.generated.resources.trip_detail_load_failed
import enrutadoseia.composeapp.generated.resources.trip_detail_unavailable_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.Clock

@Composable
fun RouteDetailPassengerScreen(
    viewModel: RouteDetailPassengerViewModel,
    onBackClick: () -> Unit,
    onBookingCreated: () -> Unit,
    onOpenDriverProfile: (String) -> Unit,
    onSearchAnotherTrip: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val notificationPermission = rememberNotificationPermissionState()

    // Right after requesting a seat is when a notification about the driver's answer matters.
    LaunchedEffect(state.bookingRequestSent) {
        if (state.bookingRequestSent) notificationPermission.request()
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RouteDetailPassengerEvent.NavigateBack -> onBackClick()
            RouteDetailPassengerEvent.NavigateToPassengerBookings -> onBookingCreated()
            RouteDetailPassengerEvent.NavigateToSearch -> onSearchAnotherTrip()
            is RouteDetailPassengerEvent.NavigateToDriverProfile -> onOpenDriverProfile(event.userId)
        }
    }

    RouteDetailPassengerContent(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailPassengerContent(
    state: RouteDetailPassengerUiState,
    onAction: (RouteDetailPassengerAction) -> Unit,
    now: Long = rememberNowMs(),
) {
    val confirmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val trip = state.trip

    Scaffold(
        contentWindowInsets = ScreenInsets,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.route_detail_passenger_title),
                onBack = { onAction(RouteDetailPassengerAction.OnBackClick) },
            )
        },
        bottomBar = {
            // The driver sees their own trip without a booking bar.
            if (trip != null && !state.isOwner) BookingBar(state = state, onAction = onAction)
        },
    ) { padding ->
        when {
            state.isLoading -> DetailSkeleton(modifier = Modifier.fillMaxSize().padding(padding))
            // The trip listener retries on its own, so a failure here almost always means the
            // trip was cancelled or deleted: offer a way back rather than a retry.
            trip == null || state.loadFailed -> EmptyState(
                icon = vectorResource(Res.drawable.error_24px),
                title = stringResource(Res.string.trip_detail_unavailable_title),
                description = stringResource(Res.string.trip_detail_load_failed),
                primaryAction = ActionButton(
                    label = stringResource(Res.string.cd_back),
                    onClick = { onAction(RouteDetailPassengerAction.OnBackClick) },
                ),
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            else -> CenteredContent(ContentWidth.list, modifier = Modifier.fillMaxSize().padding(padding), gutter = 0.dp) { margin ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = Spacing.screenHorizontal, vertical = Spacing.lg).plusHorizontal(margin),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                ) {
                    item(key = "header") { TripDetailHeader(trip = trip, now = now) }
                    // The driver keeps the plain view of their own trip; a passenger is told why
                    // the seat can no longer be asked for, before the details.
                    if (state.closedReason != null && !state.isOwner) {
                        item(key = "closed") { TripClosedBanner(reason = state.closedReason) }
                    }
                    item(key = "stats") { TripStats(trip = trip, availableSeats = state.availableSeats) }
                    item(key = "route") { TripRouteCard(trip = trip, meetingStop = state.meetingStop) }
                    item(key = "driver") {
                        DriverCard(
                            trip = trip,
                            rating = state.driver?.rating,
                            onClick = { onAction(RouteDetailPassengerAction.OnOpenDriverProfile) },
                        )
                    }
                    if (trip.messageToPassengers.isNotBlank()) {
                        item(key = "message") {
                            DriverMessageCard(driverName = trip.driver.name, message = trip.messageToPassengers)
                        }
                    }
                }
            }
        }
    }

    if (state.showConfirmSheet) {
        ConfirmRequestSheet(state = state, sheetState = confirmSheetState, onAction = onAction)
    }
}

private val previewNow = Clock.System.now().toEpochMilliseconds()

private val previewTrip = Trip(
    id = "t1",
    routeId = "",
    driverId = "d1",
    vehicleId = "v1",
    driver = TripDriver(name = "Carolina Restrepo"),
    vehicle = TripVehicle(brand = "Mazda", model = "3", color = "Gris"),
    origin = Place(name = "Viva Envigado", address = "", latitude = 6.17, longitude = -75.59),
    destination = Place.EIA_LAS_PALMAS,
    waypoints = listOf(Place(name = "Parque de Envigado", address = "", latitude = 6.17, longitude = -75.58)),
    departureTime = previewNow + 2 * 3_600_000L,
    seatCount = 3,
    confirmedSeats = 1,
    contributionPerPassenger = 4_000,
    messageToPassengers = "Salgo puntual. Te espero máximo 5 minutos frente a la iglesia.",
    status = TripStatus.Active,
)

@Preview
@Composable
private fun RouteDetailPassengerContentPreview() {
    CarpoolTheme {
        RouteDetailPassengerContent(
            state = RouteDetailPassengerUiState(
                isLoading = false,
                trip = previewTrip,
                driver = PublicProfile(id = "d1", name = "Carolina Restrepo", rating = RatingSummary(4.8, 27)),
                meetingStop = TripMeetingStop(pathIndex = 1, isDropoff = false),
            ),
            onAction = {},
            now = previewNow,
        )
    }
}

@Preview
@Composable
private fun RouteDetailPassengerClosedPreview() {
    CarpoolTheme {
        RouteDetailPassengerContent(
            state = RouteDetailPassengerUiState(
                isLoading = false,
                trip = previewTrip.copy(departureTime = previewNow - 3_600_000L, status = TripStatus.Completed),
                driver = PublicProfile(id = "d1", name = "Carolina Restrepo", rating = RatingSummary(4.8, 27)),
                closedReason = TripClosedReason.Finished,
            ),
            onAction = {},
            now = previewNow,
        )
    }
}
