package com.juanpablo0612.carpool.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route

    @Serializable
    data object ForgotPassword : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object CreateRoute : Route

    @Serializable
    data object RegisterVehicle : Route
}
