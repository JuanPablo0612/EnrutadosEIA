package com.juanpablo0612.carpool.presentation.place.selector

sealed class PlaceSelectorMode {
    data object Origin : PlaceSelectorMode()
    data object Destination : PlaceSelectorMode()
    data object Waypoint : PlaceSelectorMode()
    data object MyPlaces : PlaceSelectorMode()

    companion object {
        /** The wire value for [MyPlaces], so callers don't have to spell the string literal. */
        const val MY_PLACES_KEY = "MY_PLACES"

        fun fromString(s: String): PlaceSelectorMode = when (s) {
            "ORIGIN" -> Origin
            "DESTINATION" -> Destination
            "WAYPOINT" -> Waypoint
            "MY_PLACES" -> MyPlaces
            else -> Origin
        }
    }
}
