package com.juanpablo0612.carpool.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.presentation.home.components.GettingStartedCard
import com.juanpablo0612.carpool.presentation.home.components.HomeHeader
import com.juanpablo0612.carpool.presentation.home.components.HowItWorksSection
import com.juanpablo0612.carpool.presentation.home.components.LaterTripRow
import com.juanpablo0612.carpool.presentation.home.components.PendingRequestsBanner
import com.juanpablo0612.carpool.presentation.home.components.SearchEntryCard
import com.juanpablo0612.carpool.presentation.home.components.UpcomingTripCard
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.SectionHeader
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.rememberNowMs
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.home_action_publish_trip
import enrutadoseia.composeapp.generated.resources.home_later_title
import enrutadoseia.composeapp.generated.resources.home_next_up_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.Clock

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSearchTrips: () -> Unit,
    onPublishTrip: () -> Unit,
    onRegisterVehicle: () -> Unit,
    onOpenRequests: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenTripDetail: (String) -> Unit,
    onOpenPassengers: (String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            HomeEvent.NavigateToSearchTrips -> onSearchTrips()
            HomeEvent.NavigateToPublishTrip -> onPublishTrip()
            HomeEvent.NavigateToRegisterVehicle -> onRegisterVehicle()
            HomeEvent.NavigateToRequests -> onOpenRequests()
            HomeEvent.NavigateToNotifications -> onOpenNotifications()
            is HomeEvent.NavigateToTripDetail -> onOpenTripDetail(event.tripId)
            is HomeEvent.NavigateToPassengers -> onOpenPassengers(event.tripId)
        }
    }

    HomeContent(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeContent(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    now: Long = rememberNowMs(),
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onAction(HomeAction.PublishTrip) },
                icon = { Icon(vectorResource(Res.drawable.add_24px), contentDescription = null) },
                text = { Text(stringResource(Res.string.home_action_publish_trip), style = MaterialTheme.typography.titleMedium) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(HomeAction.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                state.isLoading -> ListSkeleton(
                    itemCount = 4,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = Spacing.lg),
                )
                state.error != null -> ErrorState(
                    description = stringResource(state.error.asStringResource()),
                    onRetry = { onAction(HomeAction.Refresh) },
                    modifier = Modifier.fillMaxSize(),
                )
                else -> HomeDashboard(state = state, now = now, onAction = onAction)
            }
        }
    }
}

@Composable
private fun HomeDashboard(state: HomeUiState, now: Long, onAction: (HomeAction) -> Unit) {
    LazyColumn(
        // Bottom room so the last card can scroll clear of the publish button.
        contentPadding = PaddingValues(
            start = Spacing.screenHorizontal,
            end = Spacing.screenHorizontal,
            top = Spacing.lg,
            bottom = 96.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl),
    ) {
        item(key = "header") {
            HomeHeader(
                firstName = state.user?.firstName().orEmpty(),
                now = now,
                onOpenNotifications = { onAction(HomeAction.OpenNotifications) },
            )
        }
        item(key = "search") {
            SearchEntryCard(onSearch = { onAction(HomeAction.SearchTrips) })
        }
        state.nextUp?.let { next ->
            item(key = "next_up") {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    SectionHeader(
                        title = stringResource(Res.string.home_next_up_title),
                        contentPadding = PaddingValues(0.dp),
                    )
                    UpcomingTripCard(
                        upcoming = next,
                        now = now,
                        onOpen = { onAction(HomeAction.OpenTrip(next.tripId)) },
                        onOpenPassengers = { onAction(HomeAction.OpenPassengers(next.tripId)) },
                    )
                }
            }
        }
        if (state.pendingRequestCount > 0) {
            item(key = "requests") {
                PendingRequestsBanner(
                    count = state.pendingRequestCount,
                    onClick = { onAction(HomeAction.OpenRequests) },
                )
            }
        }
        state.later?.let { later ->
            item(key = "later") {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    SectionHeader(
                        title = stringResource(Res.string.home_later_title),
                        contentPadding = PaddingValues(0.dp),
                    )
                    LaterTripRow(
                        upcoming = later,
                        now = now,
                        onOpen = { onAction(HomeAction.OpenTrip(later.tripId)) },
                    )
                }
            }
        }
        if (state.showGettingStarted) {
            item(key = "getting_started") {
                GettingStartedCard(
                    hasBookedBefore = state.hasBookedBefore,
                    hasVehicles = state.hasVehicles,
                    onSearch = { onAction(HomeAction.SearchTrips) },
                    onRegisterVehicle = { onAction(HomeAction.RegisterVehicle) },
                )
            }
        }
        if (state.showHowItWorks) {
            item(key = "how_it_works") { HowItWorksSection() }
        }
    }
}

/** The greeting uses the first name only; a full name reads stiff next to "Buenas tardes". */
private fun User.firstName(): String = (name ?: email.substringBefore('@')).trim().substringBefore(' ')

private val previewUser = User(
    id = "u1",
    email = "juan.perez@eia.edu.co",
    name = "Juan Pérez",
    isEmailVerified = true,
)

private val previewNow = Clock.System.now().toEpochMilliseconds()

private val previewTrip = Trip(
    id = "t1",
    routeId = "r1",
    driverId = "u1",
    vehicleId = "v1",
    origin = Place.EIA_LAS_PALMAS,
    destination = Place(name = "Parque de Envigado", address = "Envigado", latitude = 6.17, longitude = -75.58),
    waypoints = emptyList(),
    departureTime = previewNow + 45 * 60_000L,
    seatCount = 3,
    confirmedSeats = 2,
    status = TripStatus.Active,
)

private val previewBooking = Booking(
    id = "b1",
    tripId = "t2",
    passengerId = "u1",
    driverId = "d1",
    passengerName = "Juan Pérez",
    passengerEmail = "juan.perez@eia.edu.co",
    originName = "Parque de Envigado",
    destinationName = "EIA · Sede Las Palmas",
    departureTime = previewNow + 26 * 3_600_000L,
    status = BookingStatus.Confirmed,
    createdAt = previewNow,
)

@Preview
@Composable
private fun HomeContentNewUserPreview() {
    CarpoolTheme {
        HomeContent(
            state = HomeUiState(user = previewUser, isLoading = false),
            onAction = {},
            now = previewNow,
        )
    }
}

@Preview
@Composable
private fun HomeContentPopulatedPreview() {
    CarpoolTheme {
        HomeContent(
            state = HomeUiState(
                user = previewUser,
                isLoading = false,
                nextUp = UpcomingTrip.Driving(previewTrip),
                later = UpcomingTrip.Riding(previewBooking),
                pendingRequestCount = 2,
                hasVehicles = true,
                hasBookedBefore = true,
            ),
            onAction = {},
            now = previewNow,
        )
    }
}

@Preview
@Composable
private fun HomeContentErrorPreview() {
    CarpoolTheme {
        HomeContent(
            state = HomeUiState(user = previewUser, isLoading = false, error = HomeError.LoadFailed),
            onAction = {},
            now = previewNow,
        )
    }
}
