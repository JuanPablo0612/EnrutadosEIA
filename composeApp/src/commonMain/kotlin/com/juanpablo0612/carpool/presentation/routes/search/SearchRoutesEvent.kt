package com.juanpablo0612.carpool.presentation.routes.search

sealed class SearchRoutesEvent {
    data class NavigateToRouteDetail(val routeId: String) : SearchRoutesEvent()
}
