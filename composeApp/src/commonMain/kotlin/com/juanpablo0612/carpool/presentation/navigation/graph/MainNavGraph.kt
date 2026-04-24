package com.juanpablo0612.carpool.presentation.navigation.graph

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.juanpablo0612.carpool.presentation.bookings.driver.BookingRequestsScreen
import com.juanpablo0612.carpool.presentation.bookings.driver.BookingRequestsViewModel
import com.juanpablo0612.carpool.presentation.home.HomeScreen
import com.juanpablo0612.carpool.presentation.navigation.Route
import com.juanpablo0612.carpool.presentation.places.add.AddPlaceScreen
import com.juanpablo0612.carpool.presentation.places.add.AddPlaceViewModel
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteScreen
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteViewModel
import com.juanpablo0612.carpool.presentation.routes.detail.RouteDetailScreen
import com.juanpablo0612.carpool.presentation.routes.detail.RouteDetailViewModel
import com.juanpablo0612.carpool.presentation.routes.list.RoutesListScreen
import com.juanpablo0612.carpool.presentation.routes.list.RoutesListViewModel
import com.juanpablo0612.carpool.presentation.session.UserSession
import com.juanpablo0612.carpool.presentation.trip.create.CreateTripScreen
import com.juanpablo0612.carpool.presentation.trip.create.CreateTripViewModel
import com.juanpablo0612.carpool.presentation.trip.driver_list.DriverTripsScreen
import com.juanpablo0612.carpool.presentation.trip.driver_list.DriverTripsViewModel
import com.juanpablo0612.carpool.presentation.vehicles.list.VehiclesListScreen
import com.juanpablo0612.carpool.presentation.vehicles.list.VehiclesListViewModel
import com.juanpablo0612.carpool.presentation.vehicles.register.RegisterVehicleScreen
import com.juanpablo0612.carpool.presentation.vehicles.register.RegisterVehicleViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.mainNavGraph(
    onSwitchRole: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToCreateRoute: () -> Unit,
    onNavigateToRegisterVehicle: () -> Unit,
    onNavigateToRouteDetail: (String) -> Unit,
    onNavigateToCreateTrip: (String) -> Unit,
    onNavigateToAddPlace: () -> Unit,
    onNavigateBack: () -> Unit
) {
    composable<Route.Home> {
        val userSession: UserSession = koinInject()
        val user by userSession.user.collectAsState()
        val isDualRole = user?.let { it.isDriver && it.isPassenger } ?: false
        user?.let { u ->
            HomeScreen(
                user = u,
                isDualRole = isDualRole,
                onCreateRouteClick = onNavigateToCreateRoute,
                onSwitchRole = onSwitchRole,
                onLogout = onLogout
            )
        }
    }

    composable<Route.RoutesList> {
        val viewModel: RoutesListViewModel = koinViewModel()
        RoutesListScreen(
            viewModel = viewModel,
            onNavigateToCreateRoute = onNavigateToCreateRoute,
            onNavigateToRouteDetail = onNavigateToRouteDetail,
            onNavigateToCreateTrip = onNavigateToCreateTrip
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
            onNavigateToAddPlace = onNavigateToAddPlace
        )
    }

    composable<Route.CreateTrip> { backStackEntry ->
        val args = backStackEntry.toRoute<Route.CreateTrip>()
        val viewModel: CreateTripViewModel = koinViewModel { parametersOf(args.routeId) }
        CreateTripScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onTripPublished = onNavigateBack
        )
    }

    composable<Route.DriverTrips> {
        val viewModel: DriverTripsViewModel = koinViewModel()
        DriverTripsScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack
        )
    }

    composable<Route.VehiclesList> {
        val viewModel: VehiclesListViewModel = koinViewModel()
        VehiclesListScreen(
            viewModel = viewModel,
            onNavigateToRegisterVehicle = onNavigateToRegisterVehicle
        )
    }

    composable<Route.RegisterVehicle> {
        val viewModel: RegisterVehicleViewModel = koinViewModel()
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
