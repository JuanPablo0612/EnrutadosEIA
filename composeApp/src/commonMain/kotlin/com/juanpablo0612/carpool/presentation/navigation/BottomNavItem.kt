package com.juanpablo0612.carpool.presentation.navigation

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.bookmarks_24px
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.home_24px
import enrutadoseia.composeapp.generated.resources.inbox_24px
import enrutadoseia.composeapp.generated.resources.nav_booking_requests
import enrutadoseia.composeapp.generated.resources.nav_home
import enrutadoseia.composeapp.generated.resources.nav_my_bookings
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

    data object MyTrips : BottomNavItem<Route.DriverTrips>(
        label = Res.string.nav_my_trips,
        icon = Res.drawable.directions_car_24px,
        route = Route.DriverTrips
    )

    data object BookingRequests : BottomNavItem<Route.DriverBookingRequests>(
        label = Res.string.nav_booking_requests,
        icon = Res.drawable.inbox_24px,
        route = Route.DriverBookingRequests
    )

    data object SearchRoutes : BottomNavItem<Route.PassengerHome>(
        label = Res.string.nav_search_routes,
        icon = Res.drawable.search_24px,
        route = Route.PassengerHome
    )

    data object PassengerBookings : BottomNavItem<Route.PassengerBookings>(
        label = Res.string.nav_my_bookings,
        icon = Res.drawable.bookmarks_24px,
        route = Route.PassengerBookings
    )

    data object Profile : BottomNavItem<Route.Profile>(
        label = Res.string.nav_profile,
        icon = Res.drawable.person_24px,
        route = Route.Profile
    )
}
