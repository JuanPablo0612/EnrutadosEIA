package com.juanpablo0612.carpool.presentation.navigation

import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.presentation.mytrips.MyTripsTab
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

    /**
     * The list of places the user has saved, with delete. Distinct from [AddPlace], which is the
     * creation form.
     */
    @Serializable
    data object SavedPlaces : Route

    @Serializable
    data class RouteDetail(val routeId: String) : Route

    @Serializable
    /** Publishing a trip, starting from the saved route [routeId] or from scratch. */
    data class PublishTrip(val routeId: String? = null) : Route

    /** Publishing the coming days of the recurring route [routeId] at once. */
    @Serializable
    data class PublishWeek(val routeId: String) : Route

    /**
     * Bottom-bar "Buscar". Inicio's campus shortcuts open it on [campusId] (a campus preset id)
     * in the given direction; the bottom bar opens it with the defaults.
     */
    @Serializable
    data class SearchTrips(val campusId: String? = null, val fromCampus: Boolean = false) : Route

    /** Bottom-bar "Mis viajes", opened on [tab]. */
    @Serializable
    data class MyTrips(val tab: MyTripsTab = MyTripsTab.Passenger) : Route

    /**
     * A trip as a passenger sees it. From a search, [meetingStopIndex] marks the stop where it meets
     * the passenger (on the path `[origin, waypoints…, destination]`), and [meetingIsDropoff] says
     * whether they get off there rather than on.
     */
    @Serializable
    data class TripDetailPassenger(
        val tripId: String,
        val meetingStopIndex: Int? = null,
        val meetingIsDropoff: Boolean = false,
    ) : Route

    @Serializable
    data object DriverBookingRequests : Route

    @Serializable
    data class TripPassengers(val tripId: String) : Route

    @Serializable
    data class PassengerProfile(val userId: String) : Route

    @Serializable
    data object EmailVerification : Route

    @Serializable
    data object Profile : Route

    @Serializable
    data object Onboarding : Route

    @Serializable
    data object Notifications : Route

    @Serializable
    data object EditProfile : Route

    @Serializable
    data class TripTracking(val tripId: String) : Route

    @Serializable
    data class Chat(
        val bookingId: String,
        val tripId: String,
        /** Shown as the screen title; without it the title fell back to the literal "Chat". */
        val otherPartyName: String,
        /**
         * Initial read-only guess at nav time; the screen re-derives this live from the trip's
         * status via [tripId] so it doesn't go stale if the trip completes/cancels mid-chat.
         */
        val isReadOnly: Boolean,
    ) : Route

    @Serializable
    data class PostTripRating(
        val bookingId: String,
        val tripId: String,
        val rateeId: String,
        val rateeName: String,
        /**
         * Whether the person **being rated** drives — it selects which chip set the rater is
         * offered, not which role the rater holds. A passenger rating their driver passes `true`.
         */
        val rateeIsDriver: Boolean
    ) : Route

    @Serializable
    data class MapPicker(
        val initialLatitude: Double = Place.EIA_LAS_PALMAS.latitude,
        val initialLongitude: Double = Place.EIA_LAS_PALMAS.longitude,
    ) : Route
}
