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
import com.juanpablo0612.carpool.presentation.home.HomeScreen
import com.juanpablo0612.carpool.presentation.home.HomeViewModel
import com.juanpablo0612.carpool.presentation.mytrips.MyTripsScreen
import com.juanpablo0612.carpool.presentation.mytrips.MyTripsTab
import com.juanpablo0612.carpool.presentation.navigation.Route
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesScreen
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesViewModel
import com.juanpablo0612.carpool.presentation.session.UserSession
import com.juanpablo0612.carpool.presentation.trip.driverlist.DriverTripsScreen
import com.juanpablo0612.carpool.presentation.trip.driverlist.DriverTripsViewModel
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerScreen
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * The four bottom-bar destinations' roots (Inicio, Buscar, Mis viajes; Perfil lives in
 * [sharedNavGraph]) plus the trip detail a search result opens.
 */
fun NavGraphBuilder.mainNavGraph(
    pendingRequestCount: () -> Int,
    onNavigateToProfile: () -> Unit,
    onPublishTrip: () -> Unit,
    onNavigateToCreateRoute: () -> Unit,
    onNavigateToRegisterVehicle: () -> Unit,
    onNavigateToRoutesList: () -> Unit,
    onNavigateToSavedPlaces: () -> Unit,
    onNavigateToSearchTrips: () -> Unit,
    onNavigateToMyTrips: (MyTripsTab?) -> Unit,
    onNavigateToDriverBookingRequests: () -> Unit,
    onNavigateToTripDetail: (String) -> Unit,
    onBookingCreated: () -> Unit,
    onNavigateToTripTracking: (String) -> Unit,
    onNavigateToPassengers: (String) -> Unit,
    onNavigateToRating: (bookingId: String, tripId: String, rateeId: String, rateeName: String) -> Unit,
    onNavigateToAddPlace: () -> Unit,
    onNavigateToChat: (bookingId: String, tripId: String, otherPartyName: String, isReadOnly: Boolean) -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<Route.Home> {
        val viewModel: HomeViewModel = koinViewModel()
        HomeScreen(
            viewModel = viewModel,
            onNavigateToProfile = onNavigateToProfile,
            onPublishTrip = onPublishTrip,
            onNavigateToCreateRoute = onNavigateToCreateRoute,
            onNavigateToRegisterVehicle = onNavigateToRegisterVehicle,
            onNavigateToRoutesList = onNavigateToRoutesList,
            onNavigateToMyTrips = onNavigateToMyTrips,
            onNavigateToDriverBookingRequests = onNavigateToDriverBookingRequests,
            onNavigateToSearchTrips = onNavigateToSearchTrips,
            onNavigateToSavedPlaces = onNavigateToSavedPlaces,
            onNavigateToTripDetail = onNavigateToTripDetail,
        )
    }

    composable<Route.SearchTrips> {
        val userSession: UserSession = koinInject()
        val user by userSession.user.collectAsState()
        user?.let { u ->
            val viewModel: SearchRoutesViewModel = koinViewModel()
            SearchRoutesScreen(
                viewModel = viewModel,
                user = u,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToTripDetail = onNavigateToTripDetail,
                onNavigateToAddPlace = onNavigateToAddPlace
            )
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    composable<Route.MyTrips> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.MyTrips>()
        MyTripsScreen(
            initialTab = args.tab,
            pendingRequestCount = pendingRequestCount(),
            onOpenBookingRequests = onNavigateToDriverBookingRequests,
            passengerContent = {
                val viewModel: PassengerBookingsViewModel = koinViewModel()
                PassengerBookingsScreen(
                    viewModel = viewModel,
                    onBackClick = onNavigateBack,
                    onNavigateToTripTracking = onNavigateToTripTracking,
                    // Here the user rides, so the ratee is always the driver.
                    onNavigateToRating = onNavigateToRating,
                    onNavigateToSearchTrips = onNavigateToSearchTrips,
                    onNavigateToChat = onNavigateToChat,
                )
            },
            driverContent = {
                val viewModel: DriverTripsViewModel = koinViewModel()
                DriverTripsScreen(
                    viewModel = viewModel,
                    onNavigateToRoutesList = onPublishTrip,
                    onNavigateToTripDetail = onNavigateToTripDetail,
                    onNavigateToPassengers = onNavigateToPassengers,
                    onNavigateToTripTracking = onNavigateToTripTracking,
                )
            },
        )
    }

    composable<Route.TripDetailPassenger> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.TripDetailPassenger>()
        val viewModel: RouteDetailPassengerViewModel = koinViewModel { parametersOf(args.tripId) }
        RouteDetailPassengerScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onBookingCreated = onBookingCreated
        )
    }
}
