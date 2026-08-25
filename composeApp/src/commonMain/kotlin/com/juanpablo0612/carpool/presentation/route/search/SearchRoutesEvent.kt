package com.juanpablo0612.carpool.presentation.route.search

sealed class SearchRoutesEvent {
    data class NavigateToTripDetail(val tripId: String) : SearchRoutesEvent()
}
