package com.juanpablo0612.carpool.presentation.navigation

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.calendar_month_24px
import enrutadoseia.composeapp.generated.resources.home_24px
import enrutadoseia.composeapp.generated.resources.nav_home
import enrutadoseia.composeapp.generated.resources.nav_my_trips
import enrutadoseia.composeapp.generated.resources.nav_profile
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

    data object SearchTrips : BottomNavItem<Route.SearchTrips>(
        label = Res.string.nav_search_routes,
        icon = Res.drawable.search_24px,
        route = Route.SearchTrips
    )

    data object MyTrips : BottomNavItem<Route.MyTrips>(
        label = Res.string.nav_my_trips,
        icon = Res.drawable.calendar_month_24px,
        route = Route.MyTrips()
    )

    data object Profile : BottomNavItem<Route.Profile>(
        label = Res.string.nav_profile,
        icon = Res.drawable.person_24px,
        route = Route.Profile
    )
}
