package com.juanpablo0612.carpool.presentation.route.list

sealed class RoutesListAction {
    data object OnCreateRouteClick : RoutesListAction()
    data class OnRouteClick(val routeId: String) : RoutesListAction()
    data class OnPublishTripClick(val routeId: String) : RoutesListAction()
    data class OnDeleteRouteClick(val routeId: String) : RoutesListAction()
    data class OnDuplicateRouteClick(val routeId: String) : RoutesListAction()
    data object OnConfirmDelete : RoutesListAction()
    data object OnDismissDelete : RoutesListAction()
    data object OnRetry : RoutesListAction()
    data object OnRefresh : RoutesListAction()
    data class OnSearchQueryChanged(val query: String) : RoutesListAction()
    data object OnDismissActionError : RoutesListAction()
    data object OnDismissDuplicateSuccess : RoutesListAction()
    data object OnBackClick : RoutesListAction()
    data object OnCommunityRoutesClick : RoutesListAction()
}
