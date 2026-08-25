package com.juanpablo0612.carpool.presentation.route.detail

sealed class RouteDetailEvent {
    data object RouteUpdated : RouteDetailEvent()
    data object NavigateBack : RouteDetailEvent()
    data class NavigateToCreateTrip(val routeId: String) : RouteDetailEvent()
}
