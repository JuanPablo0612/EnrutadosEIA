package com.juanpablo0612.carpool.presentation.routes.create

sealed class CreateRouteEvent {
    object NavigateBack : CreateRouteEvent()
    data class ShowError(val message: String) : CreateRouteEvent()
    object RouteCreated : CreateRouteEvent()
}
