package com.juanpablo0612.carpool.presentation.routes.create

sealed class CreateRouteEvent {
    data object NavigateBack : CreateRouteEvent()
    data class ShowError(val error: CreateRouteError) : CreateRouteEvent()
    data object RouteCreated : CreateRouteEvent()
}
