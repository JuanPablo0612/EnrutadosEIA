package com.juanpablo0612.carpool.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route

    @Serializable
    data object ForgotPassword : Route

    @Serializable
    data object RoleSelector : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object CreateRoute : Route

    @Serializable
    data object RegisterVehicle : Route

    @Serializable
    data object RoutesList : Route

    @Serializable
    data object VehiclesList : Route

    @Serializable
    data object AddPlace : Route

    @Serializable
    data class RouteDetail(val routeId: String) : Route

    @Serializable
    data object PassengerHome : Route

    @Serializable
    data class RouteDetailPassenger(val routeId: String) : Route

    @Serializable
    data object PassengerBookings : Route

    @Serializable
    data object DriverBookingRequests : Route
}
