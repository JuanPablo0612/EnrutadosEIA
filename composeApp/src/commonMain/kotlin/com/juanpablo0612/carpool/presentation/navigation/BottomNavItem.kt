package com.juanpablo0612.carpool.presentation.navigation

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.home_24px
import enrutadoseia.composeapp.generated.resources.nav_create_route
import enrutadoseia.composeapp.generated.resources.nav_home
import enrutadoseia.composeapp.generated.resources.nav_register_vehicle
import enrutadoseia.composeapp.generated.resources.person_24px
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

    data object CreateRoute : BottomNavItem<Route.CreateRoute>(
        label = Res.string.nav_create_route,
        icon = Res.drawable.add_24px,
        route = Route.CreateRoute
    )

    data object RegisterVehicle : BottomNavItem<Route.RegisterVehicle>(
        label = Res.string.nav_register_vehicle,
        icon = Res.drawable.person_24px,
        route = Route.RegisterVehicle
    )
}
