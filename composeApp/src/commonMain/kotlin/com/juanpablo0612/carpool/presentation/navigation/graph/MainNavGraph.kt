package com.juanpablo0612.carpool.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.juanpablo0612.carpool.presentation.home.HomeScreen
import com.juanpablo0612.carpool.presentation.navigation.Route
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteScreen
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteViewModel
import com.juanpablo0612.carpool.presentation.vehicles.register.RegisterVehicleScreen
import com.juanpablo0612.carpool.presentation.vehicles.register.RegisterVehicleViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.mainNavGraph(
    onNavigateToCreateRoute: () -> Unit,
    onNavigateBack: () -> Unit
) {
    composable<Route.Home> {
        HomeScreen(onCreateRouteClick = onNavigateToCreateRoute)
    }

    composable<Route.CreateRoute> {
        val viewModel: CreateRouteViewModel = koinViewModel()
        CreateRouteScreen(
            viewModel = viewModel,
            onBackClick = onNavigateBack,
            onRouteCreated = onNavigateBack
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
}
