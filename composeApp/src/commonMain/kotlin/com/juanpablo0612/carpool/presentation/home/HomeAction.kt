package com.juanpablo0612.carpool.presentation.home

import com.juanpablo0612.carpool.presentation.route.search.SearchShortcut

sealed class HomeAction {
    data object SearchTrips : HomeAction()
    data class SearchShortcutSelected(val shortcut: SearchShortcut) : HomeAction()
    /** Opens publishing, or explains that a vehicle comes first when there is none. */
    data object PublishTrip : HomeAction()
    data object DismissVehicleRequired : HomeAction()
    data object RegisterVehicle : HomeAction()
    data object DismissVehicleSuggestion : HomeAction()
    data object OpenRequests : HomeAction()
    data object OpenNotifications : HomeAction()
    data object Refresh : HomeAction()
    data class OpenTrip(val tripId: String) : HomeAction()
    data class OpenPassengers(val tripId: String) : HomeAction()
}
