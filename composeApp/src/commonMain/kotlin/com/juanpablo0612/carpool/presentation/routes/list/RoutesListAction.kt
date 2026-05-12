package com.juanpablo0612.carpool.presentation.routes.list

sealed class RoutesListAction {
    data object OnCreateRouteClick : RoutesListAction()
    data class OnRouteClick(val routeId: String) : RoutesListAction()
    data class OnPublishTripClick(val routeId: String) : RoutesListAction()
    data class OnDeleteRouteClick(val routeId: String) : RoutesListAction()
    data class OnDuplicateRouteClick(val routeId: String) : RoutesListAction()
    data object OnConfirmDelete : RoutesListAction()
    data object OnDismissDelete : RoutesListAction()
    data object OnBackClick : RoutesListAction()
}
