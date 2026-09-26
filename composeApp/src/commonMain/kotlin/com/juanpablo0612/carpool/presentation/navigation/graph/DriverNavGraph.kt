package com.juanpablo0612.carpool.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.juanpablo0612.carpool.presentation.booking.driver.BookingRequestsScreen
import com.juanpablo0612.carpool.presentation.booking.driver.BookingRequestsViewModel
import com.juanpablo0612.carpool.presentation.booking.driver.TripPassengersScreen
import com.juanpablo0612.carpool.presentation.booking.driver.TripPassengersViewModel
import com.juanpablo0612.carpool.presentation.navigation.Route
import com.juanpablo0612.carpool.presentation.profile.passenger.PassengerProfileScreen
import com.juanpablo0612.carpool.presentation.profile.passenger.PassengerProfileViewModel
import com.juanpablo0612.carpool.presentation.route.create.CreateRouteScreen
import com.juanpablo0612.carpool.presentation.route.create.CreateRouteViewModel
import com.juanpablo0612.carpool.presentation.route.detail.RouteDetailScreen
import com.juanpablo0612.carpool.presentation.route.detail.RouteDetailViewModel
import com.juanpablo0612.carpool.presentation.route.list.RoutesListScreen
import com.juanpablo0612.carpool.presentation.route.list.RoutesListViewModel
import com.juanpablo0612.carpool.presentation.trip.publish.PublishTripScreen
import com.juanpablo0612.carpool.presentation.trip.publish.PublishTripViewModel
import com.juanpablo0612.carpool.presentation.vehicle.list.VehiclesListScreen
import com.juanpablo0612.carpool.presentation.vehicle.list.VehiclesListViewModel
import com.juanpablo0612.carpool.presentation.vehicle.register.RegisterVehicleScreen
import com.juanpablo0612.carpool.presentation.vehicle.register.RegisterVehicleViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Screens for trips you drive: routes, publishing, vehicles and booking requests. Every user can
 * reach them; there is no driver role.
 */
fun NavGraphBuilder.driverNavGraph(
    onNavigateToCreateRoute: () -> Unit,
    onNavigateToRegisterVehicle: () -> Unit,
    onNavigateToEditVehicle: (String) -> Unit,
    onNavigateToRouteDetail: (String) -> Unit,
    onNavigateToPublishTrip: (String?) -> Unit,
    onNavigateToAddPlace: () -> Unit,
    onNavigateToRoutesList: () -> Unit,
    onNavigateToVehiclesList: () -> Unit,
    onTripPublished: () -> Unit,
    onNavigateToTripDetail: (String) -> Unit,
    onNavigateToTripTracking: (String) -> Unit,
    onNavigateToPassengers: (String) -> Unit,
    onNavigateToPassengerProfile: (String) -> Unit,
    onNavigateToRating: (bookingId: String, tripId: String, rateeId: String, rateeName: String) -> Unit,
    onNavigateToChat: (bookingId: String, tripId: String, otherPartyName: String, isReadOnly: Boolean) -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<Route.RoutesList> {
        val viewModel: RoutesListViewModel = koinViewModel()
        RoutesListScreen(
            viewModel = viewModel,
            onNavigateToCreateRoute = onNavigateToCreateRoute,
            onNavigateToRouteDetail = onNavigateToRouteDetail,
            onNavigateToPublishTrip = onNavigateToPublishTrip,
            onBackClick = onNavigateBack
        )
    }

    composable<Route.CreateRoute> {
        val viewModel: CreateRouteViewModel = koinViewModel()
        CreateRouteScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onRouteCreated = onNavigateBack,
            onNavigateToAddPlace = onNavigateToAddPlace
        )
    }

    composable<Route.RouteDetail> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.RouteDetail>()
        val viewModel: RouteDetailViewModel = koinViewModel { parametersOf(args.routeId) }
        RouteDetailScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onNavigateToAddPlace = onNavigateToAddPlace,
            onNavigateToPublishTrip = onNavigateToPublishTrip
        )
    }

    composable<Route.PublishTrip> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.PublishTrip>()
        val viewModel: PublishTripViewModel = koinViewModel { parametersOf(args.routeId) }
        PublishTripScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onTripPublished = onTripPublished,
            onNavigateToRegisterVehicle = onNavigateToRegisterVehicle,
            onNavigateToAddPlace = onNavigateToAddPlace,
        )
    }

    composable<Route.TripPassengers> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.TripPassengers>()
        val viewModel: TripPassengersViewModel = koinViewModel { parametersOf(args.tripId) }
        TripPassengersScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onNavigateToPassengerProfile = onNavigateToPassengerProfile,
            onNavigateToRating = onNavigateToRating,
            onNavigateToChat = onNavigateToChat,
        )
    }

    composable<Route.VehiclesList> {
        val viewModel: VehiclesListViewModel = koinViewModel()
        VehiclesListScreen(
            viewModel = viewModel,
            onNavigateToRegisterVehicle = onNavigateToRegisterVehicle,
            onNavigateToEditVehicle = onNavigateToEditVehicle,
            onNavigateToTripDetail = onNavigateToTripDetail,
            onBackClick = onNavigateBack
        )
    }

    composable<Route.RegisterVehicle> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.RegisterVehicle>()
        val viewModel: RegisterVehicleViewModel = koinViewModel { parametersOf(args.vehicleId) }
        RegisterVehicleScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onVehicleRegistered = onNavigateBack
        )
    }

    composable<Route.DriverBookingRequests> {
        val viewModel: BookingRequestsViewModel = koinViewModel()
        BookingRequestsScreen(
            viewModel = viewModel,
            onNavigateToPassengerProfile = onNavigateToPassengerProfile,
            onNavigateToRating = onNavigateToRating,
            onNavigateToChat = onNavigateToChat,
            onBackClick = onNavigateBack,
        )
    }

    composable<Route.PassengerProfile> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.PassengerProfile>()
        val viewModel: PassengerProfileViewModel = koinViewModel { parametersOf(args.userId) }
        PassengerProfileScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
        )
    }
}
