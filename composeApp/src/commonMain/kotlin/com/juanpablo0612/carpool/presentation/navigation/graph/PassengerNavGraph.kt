package com.juanpablo0612.carpool.presentation.navigation.graph

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.juanpablo0612.carpool.presentation.bookings.passenger.PassengerBookingsScreen
import com.juanpablo0612.carpool.presentation.bookings.passenger.PassengerBookingsViewModel
import com.juanpablo0612.carpool.presentation.navigation.Route
import com.juanpablo0612.carpool.presentation.routes.passenger_detail.RouteDetailPassengerScreen
import com.juanpablo0612.carpool.presentation.routes.passenger_detail.RouteDetailPassengerViewModel
import com.juanpablo0612.carpool.presentation.routes.search.SearchRoutesScreen
import com.juanpablo0612.carpool.presentation.routes.search.SearchRoutesViewModel
import com.juanpablo0612.carpool.presentation.session.UserSession
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.passengerNavGraph(
    onSwitchRole: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToRouteDetail: (String) -> Unit,
    onNavigateToPassengerBookings: () -> Unit,
    onNavigateBack: () -> Unit
) {
    composable<Route.PassengerHome> {
        val userSession: UserSession = koinInject()
        val user by userSession.user.collectAsState()
        val isDualRole = user?.let { it.isDriver && it.isPassenger } ?: false
        user?.let { u ->
            val viewModel: SearchRoutesViewModel = koinViewModel()
            SearchRoutesScreen(
                viewModel = viewModel,
                user = u,
                isDualRole = isDualRole,
                onSwitchRole = onSwitchRole,
                onLogout = onLogout,
                onNavigateToRouteDetail = onNavigateToRouteDetail
            )
        }
    }

    composable<Route.RouteDetailPassenger> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.RouteDetailPassenger>()
        val viewModel: RouteDetailPassengerViewModel = koinViewModel { parametersOf(args.routeId) }
        RouteDetailPassengerScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onBookingCreated = onNavigateToPassengerBookings
        )
    }

    composable<Route.PassengerBookings> {
        val viewModel: PassengerBookingsViewModel = koinViewModel()
        PassengerBookingsScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack
        )
    }
}
