package com.juanpablo0612.carpool.presentation.routes.search

sealed class SearchRoutesEvent {
    data class NavigateToTripDetail(val tripId: String) : SearchRoutesEvent()
}
