package com.juanpablo0612.carpool.presentation.routes.create

sealed class CreateRouteEvent {
    data object NavigateBack : CreateRouteEvent()
    data object RouteCreated : CreateRouteEvent()
}
