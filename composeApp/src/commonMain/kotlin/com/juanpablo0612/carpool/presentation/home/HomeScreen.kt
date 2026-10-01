package com.juanpablo0612.carpool.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.presentation.booking.components.PendingRequestsBanner
import com.juanpablo0612.carpool.presentation.home.components.HomeHeader
import com.juanpablo0612.carpool.presentation.home.components.HowItWorksSection
import com.juanpablo0612.carpool.presentation.home.components.HowToStartSection
import com.juanpablo0612.carpool.presentation.home.components.LaterTripRow
import com.juanpablo0612.carpool.presentation.home.components.SearchEntryCard
import com.juanpablo0612.carpool.presentation.home.components.UpcomingTripCard
import com.juanpablo0612.carpool.presentation.home.components.VehicleSuggestionCard
import com.juanpablo0612.carpool.presentation.route.search.SearchShortcut
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.SectionHeader
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.CenteredContent
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.plusHorizontal
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
    onSearchTrips: (SearchShortcut?) -> Unit,
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
            is HomeEvent.NavigateToSearchTrips -> onSearchTrips(event.shortcut)
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
    // Measured rather than assumed: the extended FAB grows with the font scale, and the list's
    // last card must still scroll clear of it.
    var fabHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Scaffold(
        contentWindowInsets = ScreenInsets,
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (state.canPublishTrip) {
                ExtendedFloatingActionButton(
                    onClick = { onAction(HomeAction.PublishTrip) },
                    modifier = Modifier.onSizeChanged { fabHeight = with(density) { it.height.toDp() } },
                    icon = { Icon(vectorResource(Res.drawable.add_24px), contentDescription = null) },
                    text = { Text(stringResource(Res.string.home_action_publish_trip), style = MaterialTheme.typography.titleMedium) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                )
            }
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
                else -> HomeDashboard(
                    state = state,
                    now = now,
                    onAction = onAction,
                    // The FAB sits Spacing.lg above the bottom; Spacing.xl more keeps a gap.
                    bottomClearance = if (state.canPublishTrip) fabHeight + Spacing.lg + Spacing.xl else Spacing.xl,
                )
            }
        }
    }
}

@Composable
private fun HomeDashboard(state: HomeUiState, now: Long, onAction: (HomeAction) -> Unit, bottomClearance: Dp) {
    CenteredContent(ContentWidth.list, modifier = Modifier.fillMaxSize(), gutter = 0.dp) { margin ->
        // One lane on phones; two on wide windows, where the dashboard's cards sit side by side
        // instead of leaving a long single column. Staggered because the cards differ in height.
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(ContentWidth.gridCell),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Spacing.screenHorizontal,
                end = Spacing.screenHorizontal,
                top = Spacing.lg,
                bottom = bottomClearance,
            ).plusHorizontal(margin),
            verticalItemSpacing = Spacing.xl,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xl),
        ) {
            item(key = "header", span = StaggeredGridItemSpan.FullLine) {
                HomeHeader(
                    firstName = state.user?.firstName().orEmpty(),
                    now = now,
                    onOpenNotifications = { onAction(HomeAction.OpenNotifications) },
                )
            }
            item(key = "search") {
                SearchEntryCard(
                    onSearch = { onAction(HomeAction.SearchTrips) },
                    onShortcut = { onAction(HomeAction.SearchShortcutSelected(it)) },
                )
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
            when (state.welcome) {
                HomeWelcome.ChooseHowToStart -> {
                    item(key = "how_to_start") {
                        HowToStartSection(
                            onFindSeat = { onAction(HomeAction.SearchTrips) },
                            onRegisterVehicle = { onAction(HomeAction.RegisterVehicle) },
                        )
                    }
                    item(key = "how_it_works") { HowItWorksSection() }
                }
                HomeWelcome.SuggestVehicle -> item(key = "vehicle_suggestion") {
                    VehicleSuggestionCard(
                        onRegisterVehicle = { onAction(HomeAction.RegisterVehicle) },
                        onDismiss = { onAction(HomeAction.DismissVehicleSuggestion) },
                    )
                }
                HomeWelcome.None -> Unit
            }
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
private fun HomeContentRiderPreview() {
    CarpoolTheme {
        HomeContent(
            state = HomeUiState(
                user = previewUser,
                isLoading = false,
                nextUp = UpcomingTrip.Riding(previewBooking),
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
