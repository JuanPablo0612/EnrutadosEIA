package com.juanpablo0612.carpool.presentation.routes.list

sealed class RoutesListAction {
    data object OnCreateRouteClick : RoutesListAction()
    data class OnRouteClick(val routeId: String) : RoutesListAction()
}
