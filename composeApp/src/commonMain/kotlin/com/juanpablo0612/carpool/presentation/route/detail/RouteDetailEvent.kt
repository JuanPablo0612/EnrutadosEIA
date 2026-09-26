package com.juanpablo0612.carpool.presentation.route.detail

sealed class RouteDetailEvent {
    data object NavigateBack : RouteDetailEvent()
    data class NavigateToPublishTrip(val routeId: String) : RouteDetailEvent()
}
