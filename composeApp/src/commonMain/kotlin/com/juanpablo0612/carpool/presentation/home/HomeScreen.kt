package com.juanpablo0612.carpool.presentation.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.presentation.home.components.HomeDashboard
import com.juanpablo0612.carpool.presentation.mytrips.MyTripsTab
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.home_reject_confirm_body
import enrutadoseia.composeapp.generated.resources.home_reject_confirm_title
import enrutadoseia.composeapp.generated.resources.nav_home
import enrutadoseia.composeapp.generated.resources.reject_confirm_button
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToProfile: () -> Unit,
    onPublishTrip: () -> Unit,
    onNavigateToCreateRoute: () -> Unit,
    onNavigateToRegisterVehicle: () -> Unit,
    onNavigateToRoutesList: () -> Unit,
    onNavigateToMyTrips: (MyTripsTab?) -> Unit,
    onNavigateToDriverBookingRequests: () -> Unit,
    onNavigateToSearchTrips: () -> Unit,
    onNavigateToSavedPlaces: () -> Unit,
    onNavigateToTripDetail: (String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            HomeEvent.NavigateToPublishTrip -> onPublishTrip()
            HomeEvent.NavigateToCreateRoute -> onNavigateToCreateRoute()
            HomeEvent.NavigateToRegisterVehicle -> onNavigateToRegisterVehicle()
            HomeEvent.NavigateToRoutesList -> onNavigateToRoutesList()
            is HomeEvent.NavigateToMyTrips -> onNavigateToMyTrips(event.tab)
            HomeEvent.NavigateToDriverBookingRequests -> onNavigateToDriverBookingRequests()
            HomeEvent.NavigateToSearchTrips -> onNavigateToSearchTrips()
            HomeEvent.NavigateToSavedPlaces -> onNavigateToSavedPlaces()
            is HomeEvent.NavigateToTripDetail -> onNavigateToTripDetail(event.tripId)
        }
    }

    HomeContent(
        state = state,
        onNavigateToProfile = onNavigateToProfile,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeContent(
    state: HomeUiState,
    onNavigateToProfile: () -> Unit,
    onAction: (HomeAction) -> Unit,
) {
    val pullRefreshState = rememberPullToRefreshState()

    if (state.pendingRejectBookingId != null) {
        ConfirmDialog(
            title = stringResource(Res.string.home_reject_confirm_title),
            description = stringResource(Res.string.home_reject_confirm_body),
            confirmText = stringResource(Res.string.reject_confirm_button),
            onConfirm = { onAction(HomeAction.OnConfirmReject) },
            onDismiss = { onAction(HomeAction.OnDismissRejectConfirm) },
            isDestructive = true,
        )
    }

    Scaffold(
        topBar = {
            state.user?.let { user ->
                CarpoolTopBar(
                    title = stringResource(Res.string.nav_home),
                    user = user,
                    onAvatarClick = onNavigateToProfile,
                )
            }
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(HomeAction.Refresh) },
            state = pullRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                state.isLoading -> {
                    ListSkeleton(
                        itemCount = 4,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = Spacing.lg),
                    )
                }
                state.error is HomeError.LoadFailed -> {
                    ErrorState(
                        description = stringResource(state.error.asStringResource()),
                        onRetry = { onAction(HomeAction.Refresh) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                else -> {
                    HomeDashboard(state = state, onAction = onAction)
                }
            }
        }
    }
}

private val previewUser = User(
    id = "u1",
    email = "juan.perez@eia.edu.co",
    name = "Juan Pérez",
    isEmailVerified = true,
    isPassenger = true,
    isDriver = true,
)

private val previewTrip = Trip(
    id = "t1",
    routeId = "r1",
    driverId = "u1",
    vehicleId = "v1",
    origin = Place.UNIVERSITY_EIA,
    destination = Place(name = "Centro Comercial Santafé", address = "Cra 43A, Medellín", latitude = 6.22, longitude = -75.57),
    waypoints = emptyList(),
    departureTime = Clock.System.now().toEpochMilliseconds() + 3_600_000L,
    seatCount = 3,
    status = TripStatus.Active,
)

private val previewPendingRequests = listOf(
    Booking(
        id = "b1",
        tripId = "t1",
        passengerId = "p1",
        driverId = "u1",
        passengerName = "Laura Gómez",
        passengerEmail = "laura.gomez@eia.edu.co",
        originName = "EIA — Sede Las Palmas",
        destinationName = "Centro Comercial Santafé",
        departureTime = Clock.System.now().toEpochMilliseconds() + 3_600_000L,
        status = BookingStatus.Pending,
        createdAt = Clock.System.now().toEpochMilliseconds(),
    ),
)

@Preview
@Composable
private fun HomeContentLoadingPreview() {
    CarpoolTheme {
        HomeContent(
            state = HomeUiState(user = previewUser, isLoading = true),
            onNavigateToProfile = {},
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun HomeContentNewUserPreview() {
    CarpoolTheme {
        HomeContent(
            state = HomeUiState(
                user = previewUser,
                isLoading = false,
                hasVehicles = false,
                hasRoutes = false,
            ),
            onNavigateToProfile = {},
            onAction = {},
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
                hasVehicles = true,
                hasRoutes = true,
                nextTrip = previewTrip,
                pendingRequests = previewPendingRequests,
                tripsThisMonth = 5,
                passengersThisMonth = 12,
            ),
            onNavigateToProfile = {},
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun HomeContentErrorPreview() {
    CarpoolTheme {
        HomeContent(
            state = HomeUiState(
                user = previewUser,
                isLoading = false,
                error = HomeError.LoadFailed,
            ),
            onNavigateToProfile = {},
            onAction = {},
        )
    }
}
