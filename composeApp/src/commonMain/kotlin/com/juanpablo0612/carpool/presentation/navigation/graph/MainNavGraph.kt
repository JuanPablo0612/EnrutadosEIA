package com.juanpablo0612.carpool.presentation.navigation.graph

import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.juanpablo0612.carpool.domain.trip.model.TripMeetingStop
import com.juanpablo0612.carpool.presentation.booking.passenger.PassengerBookingsScreen
import com.juanpablo0612.carpool.presentation.booking.passenger.PassengerBookingsViewModel
import com.juanpablo0612.carpool.presentation.home.HomeScreen
import com.juanpablo0612.carpool.presentation.home.HomeViewModel
import com.juanpablo0612.carpool.presentation.mytrips.MyTripsScreen
import com.juanpablo0612.carpool.presentation.navigation.Route
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesScreen
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesViewModel
import com.juanpablo0612.carpool.presentation.route.search.SearchShortcut
import com.juanpablo0612.carpool.presentation.trip.driverlist.DriverTripsScreen
import com.juanpablo0612.carpool.presentation.trip.driverlist.DriverTripsViewModel
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerScreen
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerViewModel
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
    onNavigateToRegisterVehicle: () -> Unit,
    onNavigateToRoutesList: () -> Unit,
    /** Switches to Buscar; a shortcut opens it on that campus search. */
    onNavigateToSearchTrips: (SearchShortcut?) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToDriverBookingRequests: () -> Unit,
    onNavigateToTripDetail: (String) -> Unit,
    /** Opens a search result, marking the stop where the trip meets the passenger. */
    onNavigateToSearchResult: (tripId: String, meetingStop: TripMeetingStop?) -> Unit,
    onNavigateToUserProfile: (userId: String) -> Unit,
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
            onSearchTrips = onNavigateToSearchTrips,
            onPublishTrip = onPublishTrip,
            onRegisterVehicle = onNavigateToRegisterVehicle,
            onOpenRequests = onNavigateToDriverBookingRequests,
            onOpenNotifications = onNavigateToNotifications,
            onOpenTripDetail = onNavigateToTripDetail,
            onOpenPassengers = onNavigateToPassengers,
        )
    }

    composable<Route.SearchTrips> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.SearchTrips>()
        val viewModel: SearchRoutesViewModel = koinViewModel { parametersOf(args.campusId, args.fromCampus) }
        SearchRoutesScreen(
            viewModel = viewModel,
            onNavigateToTripDetail = onNavigateToSearchResult,
            onNavigateToAddPlace = onNavigateToAddPlace
        )
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
                    onNavigateToSearchTrips = { onNavigateToSearchTrips(null) },
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
        val meetingStop = args.meetingStopIndex?.let { TripMeetingStop(it, args.meetingIsDropoff) }
        val viewModel: RouteDetailPassengerViewModel = koinViewModel { parametersOf(args.tripId, meetingStop) }
        RouteDetailPassengerScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onBookingCreated = onBookingCreated,
            onOpenDriverProfile = onNavigateToUserProfile,
        )
    }
}
