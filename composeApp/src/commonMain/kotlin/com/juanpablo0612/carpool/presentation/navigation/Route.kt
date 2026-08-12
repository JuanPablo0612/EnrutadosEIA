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
    data class RegisterVehicle(val vehicleId: String? = null) : Route

    @Serializable
    data object RoutesList : Route

    @Serializable
    data object VehiclesList : Route

    @Serializable
    data object AddPlace : Route

    @Serializable
    data class RouteDetail(val routeId: String) : Route

    @Serializable
    data class CreateTrip(val routeId: String) : Route

    @Serializable
    data object DriverTrips : Route

    @Serializable
    data object PassengerHome : Route

    @Serializable
    data class TripDetailPassenger(val tripId: String) : Route

    @Serializable
    data object PassengerBookings : Route

    @Serializable
    data object DriverBookingRequests : Route

    @Serializable
    data object EmailVerification : Route

    @Serializable
    data object Profile : Route

    @Serializable
    data object Onboarding : Route

    @Serializable
    data object Notifications : Route

    @Serializable
    data object Safety : Route

    @Serializable
    data object EditProfile : Route

    @Serializable
    data class TripTracking(val tripId: String) : Route

    @Serializable
    data class Chat(val bookingId: String) : Route

    @Serializable
    data class PostTripRating(
        val bookingId: String,
        val tripId: String,
        val rateeId: String,
        val rateeName: String,
        val isDriver: Boolean
    ) : Route

    @Serializable
    data class MapPicker(
        val initialLatitude: Double = 6.1633,
        val initialLongitude: Double = -75.4913,
    ) : Route
}
