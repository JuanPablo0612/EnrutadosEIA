package com.juanpablo0612.carpool.presentation.navigation

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.home_24px
import enrutadoseia.composeapp.generated.resources.location_on_24px
import enrutadoseia.composeapp.generated.resources.nav_home
import enrutadoseia.composeapp.generated.resources.nav_my_routes
import enrutadoseia.composeapp.generated.resources.nav_my_vehicles
import enrutadoseia.composeapp.generated.resources.nav_search_routes
import enrutadoseia.composeapp.generated.resources.person_24px
import enrutadoseia.composeapp.generated.resources.search_24px
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class BottomNavItem<T : Any>(
    val label: StringResource,
    val icon: DrawableResource,
    val route: T
) {
    data object Home : BottomNavItem<Route.Home>(
        label = Res.string.nav_home,
        icon = Res.drawable.home_24px,
        route = Route.Home
    )

    data object MyRoutes : BottomNavItem<Route.RoutesList>(
        label = Res.string.nav_my_routes,
        icon = Res.drawable.location_on_24px,
        route = Route.RoutesList
    )

    data object MyVehicles : BottomNavItem<Route.VehiclesList>(
        label = Res.string.nav_my_vehicles,
        icon = Res.drawable.person_24px,
        route = Route.VehiclesList
    )

    data object SearchRoutes : BottomNavItem<Route.PassengerHome>(
        label = Res.string.nav_search_routes,
        icon = Res.drawable.search_24px,
        route = Route.PassengerHome
    )
}
