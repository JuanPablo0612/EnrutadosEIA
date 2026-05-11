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
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.driver_home_title
import enrutadoseia.composeapp.generated.resources.passenger_home_title
import enrutadoseia.composeapp.generated.resources.role_selector_driver_title
import enrutadoseia.composeapp.generated.resources.role_selector_passenger_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToProfile: () -> Unit,
    onSwitchRole: () -> Unit,
    onNavigateToCreateRoute: () -> Unit,
    onNavigateToRegisterVehicle: () -> Unit,
    onNavigateToRoutesList: () -> Unit,
    onNavigateToDriverTrips: () -> Unit,
    onNavigateToDriverBookingRequests: () -> Unit,
    onNavigateToSearchTrips: () -> Unit,
    onNavigateToPassengerBookings: () -> Unit,
    onNavigateToTripDetail: (String) -> Unit,
    onNavigateToTripDetailPassenger: (String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            HomeEvent.NavigateToSwitchRole -> onSwitchRole()
            HomeEvent.NavigateToCreateRoute -> onNavigateToCreateRoute()
            HomeEvent.NavigateToRegisterVehicle -> onNavigateToRegisterVehicle()
            HomeEvent.NavigateToRoutesList -> onNavigateToRoutesList()
            HomeEvent.NavigateToDriverTrips -> onNavigateToDriverTrips()
            HomeEvent.NavigateToDriverBookingRequests -> onNavigateToDriverBookingRequests()
            HomeEvent.NavigateToSearchTrips -> onNavigateToSearchTrips()
            HomeEvent.NavigateToPassengerBookings -> onNavigateToPassengerBookings()
            is HomeEvent.NavigateToTripDetail -> onNavigateToTripDetail(event.tripId)
            is HomeEvent.NavigateToTripDetailPassenger -> onNavigateToTripDetailPassenger(event.tripId)
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

    Scaffold(
        topBar = {
            state.user?.let { user ->
                CarpoolTopBar(
                    title = stringResource(
                        if (state.role == UserRole.Driver) Res.string.driver_home_title
                        else Res.string.passenger_home_title,
                    ),
                    user = user,
                    isDualRole = state.isDualRole,
                    currentRoleLabel = stringResource(
                        if (state.role == UserRole.Driver) Res.string.role_selector_driver_title
                        else Res.string.role_selector_passenger_title,
                    ),
                    onAvatarClick = onNavigateToProfile,
                    onRoleToggle = if (state.isDualRole) {
                        { onAction(HomeAction.SwitchRole) }
                    } else null,
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
                state.error != null -> {
                    ErrorState(
                        description = state.error,
                        onRetry = { onAction(HomeAction.Refresh) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                isOnboardingEmpty(state) -> {
                    HomeDashboardEmpty(state = state, onAction = onAction)
                }
                else -> {
                    HomeDashboard(state = state, onAction = onAction)
                }
            }
        }
    }
}

private fun isOnboardingEmpty(state: HomeUiState): Boolean =
    state.role == UserRole.Driver &&
            !state.hasVehicles && !state.hasRoutes &&
            state.nextTrip == null && state.pendingRequests.isEmpty()
