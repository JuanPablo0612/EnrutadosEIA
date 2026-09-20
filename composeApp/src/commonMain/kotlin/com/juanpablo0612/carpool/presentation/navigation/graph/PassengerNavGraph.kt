package com.juanpablo0612.carpool.presentation.navigation.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.juanpablo0612.carpool.presentation.booking.passenger.PassengerBookingsScreen
import com.juanpablo0612.carpool.presentation.booking.passenger.PassengerBookingsViewModel
import com.juanpablo0612.carpool.presentation.navigation.Route
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerScreen
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerViewModel
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesScreen
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesViewModel
import com.juanpablo0612.carpool.presentation.session.UserSession
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.passengerNavGraph(
    onSwitchRole: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToTripDetail: (String) -> Unit,
    onNavigateToPassengerBookings: () -> Unit,
    onNavigateToTripTracking: (String) -> Unit,
    onNavigateToRating: (bookingId: String, tripId: String, rateeId: String, rateeName: String) -> Unit,
    onNavigateToAddPlace: () -> Unit,
    onNavigateToSearchTrips: () -> Unit,
    onNavigateToChat: (bookingId: String, tripId: String, otherPartyName: String, isReadOnly: Boolean) -> Unit,
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
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToTripDetail = onNavigateToTripDetail,
                onNavigateToAddPlace = onNavigateToAddPlace
            )
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    composable<Route.TripDetailPassenger> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.TripDetailPassenger>()
        val viewModel: RouteDetailPassengerViewModel = koinViewModel { parametersOf(args.tripId) }
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
            onBackClick = onNavigateBack,
            onNavigateToTripTracking = onNavigateToTripTracking,
            onNavigateToRating = onNavigateToRating,
            onNavigateToSearchTrips = onNavigateToSearchTrips,
            onNavigateToChat = onNavigateToChat
        )
    }
}
