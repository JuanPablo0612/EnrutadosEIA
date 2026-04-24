package com.juanpablo0612.carpool.presentation.routes.search

sealed class SearchRoutesAction {
    data class OnQueryChanged(val query: String) : SearchRoutesAction()
    data class OnTripClick(val tripId: String) : SearchRoutesAction()
}
