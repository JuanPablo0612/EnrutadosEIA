package com.juanpablo0612.carpool.presentation.route.create

sealed class CreateRouteEvent {
    data object NavigateBack : CreateRouteEvent()
    data object RouteCreated : CreateRouteEvent()
}
