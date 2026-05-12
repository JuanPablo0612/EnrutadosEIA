package com.juanpablo0612.carpool.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.juanpablo0612.carpool.presentation.bookings.driver.BookingRequestsScreen
import com.juanpablo0612.carpool.presentation.bookings.driver.BookingRequestsViewModel
import com.juanpablo0612.carpool.presentation.home.HomeScreen
import com.juanpablo0612.carpool.presentation.home.HomeViewModel
import com.juanpablo0612.carpool.presentation.navigation.Route
import com.juanpablo0612.carpool.presentation.places.add.AddPlaceScreen
import com.juanpablo0612.carpool.presentation.places.add.AddPlaceViewModel
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteScreen
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteViewModel
import com.juanpablo0612.carpool.presentation.routes.detail.RouteDetailScreen
import com.juanpablo0612.carpool.presentation.routes.detail.RouteDetailViewModel
import com.juanpablo0612.carpool.presentation.routes.list.RoutesListScreen
import com.juanpablo0612.carpool.presentation.routes.list.RoutesListViewModel
import com.juanpablo0612.carpool.presentation.trip.create.CreateTripScreen
import com.juanpablo0612.carpool.presentation.trip.create.CreateTripViewModel
import com.juanpablo0612.carpool.presentation.trip.driver_list.DriverTripsScreen
import com.juanpablo0612.carpool.presentation.trip.driver_list.DriverTripsViewModel
import com.juanpablo0612.carpool.presentation.vehicles.list.VehiclesListScreen
import com.juanpablo0612.carpool.presentation.vehicles.list.VehiclesListViewModel
import com.juanpablo0612.carpool.presentation.vehicles.register.RegisterVehicleScreen
import com.juanpablo0612.carpool.presentation.vehicles.register.RegisterVehicleViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.mainNavGraph(
    onSwitchRole: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCreateRoute: () -> Unit,
    onNavigateToRegisterVehicle: () -> Unit,
    onNavigateToEditVehicle: (String) -> Unit,
    onNavigateToRouteDetail: (String) -> Unit,
    onNavigateToCreateTrip: (String) -> Unit,
    onNavigateToAddPlace: () -> Unit,
    onNavigateToRoutesList: () -> Unit,
    onNavigateToDriverTrips: () -> Unit,
    onNavigateToDriverBookingRequests: () -> Unit,
    onNavigateToSearchTrips: () -> Unit,
    onNavigateToPassengerBookings: () -> Unit,
    onNavigateToTripDetail: (String) -> Unit,
    onNavigateToTripDetailPassenger: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<Route.Home> {
        val viewModel: HomeViewModel = koinViewModel()
        HomeScreen(
            viewModel = viewModel,
            onNavigateToProfile = onNavigateToProfile,
            onSwitchRole = onSwitchRole,
            onNavigateToCreateRoute = onNavigateToCreateRoute,
            onNavigateToRegisterVehicle = onNavigateToRegisterVehicle,
            onNavigateToRoutesList = onNavigateToRoutesList,
            onNavigateToDriverTrips = onNavigateToDriverTrips,
            onNavigateToDriverBookingRequests = onNavigateToDriverBookingRequests,
            onNavigateToSearchTrips = onNavigateToSearchTrips,
            onNavigateToPassengerBookings = onNavigateToPassengerBookings,
            onNavigateToTripDetail = onNavigateToTripDetail,
            onNavigateToTripDetailPassenger = onNavigateToTripDetailPassenger,
        )
    }

    composable<Route.RoutesList> {
        val viewModel: RoutesListViewModel = koinViewModel()
        RoutesListScreen(
            viewModel = viewModel,
            onNavigateToCreateRoute = onNavigateToCreateRoute,
            onNavigateToRouteDetail = onNavigateToRouteDetail,
            onNavigateToCreateTrip = onNavigateToCreateTrip,
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
            onNavigateToCreateTrip = onNavigateToCreateTrip
        )
    }

    composable<Route.CreateTrip> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.CreateTrip>()
        val viewModel: CreateTripViewModel = koinViewModel { parametersOf(args.routeId) }
        CreateTripScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onTripPublished = onNavigateBack,
            onNavigateToRegisterVehicle = onNavigateToRegisterVehicle,
            onNavigateToVehiclesList = { onNavigateBack() }
        )
    }

    composable<Route.DriverTrips> {
        val viewModel: DriverTripsViewModel = koinViewModel()
        DriverTripsScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onNavigateToRoutesList = onNavigateToRoutesList,
            onNavigateToTripDetail = onNavigateToTripDetail,
            onNavigateToPassengers = { /* TODO: wire when passenger management screen exists */ }
        )
    }

    composable<Route.VehiclesList> {
        val viewModel: VehiclesListViewModel = koinViewModel()
        VehiclesListScreen(
            viewModel = viewModel,
            onNavigateToRegisterVehicle = onNavigateToRegisterVehicle,
            onNavigateToEditVehicle = onNavigateToEditVehicle,
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

    composable<Route.AddPlace> {
        val viewModel: AddPlaceViewModel = koinViewModel()
        AddPlaceScreen(
            viewModel = viewModel,
            onBack = onNavigateBack,
            onPlaceSaved = onNavigateBack
        )
    }

    composable<Route.DriverBookingRequests> {
        val viewModel: BookingRequestsViewModel = koinViewModel()
        BookingRequestsScreen(viewModel = viewModel)
    }
}
