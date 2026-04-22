package com.juanpablo0612.carpool.presentation.routes.detail

sealed class RouteDetailEvent {
    data object RouteUpdated : RouteDetailEvent()
    data object NavigateBack : RouteDetailEvent()
}
