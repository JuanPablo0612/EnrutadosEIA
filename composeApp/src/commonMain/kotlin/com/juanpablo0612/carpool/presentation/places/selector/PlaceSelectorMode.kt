package com.juanpablo0612.carpool.presentation.places.selector

sealed class PlaceSelectorMode {
    data object Origin : PlaceSelectorMode()
    data object Destination : PlaceSelectorMode()
    data object Waypoint : PlaceSelectorMode()
    data object MyPlaces : PlaceSelectorMode()

    companion object {
        fun fromString(s: String): PlaceSelectorMode = when (s) {
            "ORIGIN" -> Origin
            "DESTINATION" -> Destination
            "WAYPOINT" -> Waypoint
            "MY_PLACES" -> MyPlaces
            else -> Origin
        }
    }
}
