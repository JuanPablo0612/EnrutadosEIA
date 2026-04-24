package com.juanpablo0612.carpool.presentation.routes.detail

import com.juanpablo0612.carpool.domain.places.model.Place

sealed class RouteDetailAction {
    data class OnRemoveWaypoint(val index: Int) : RouteDetailAction()
    data class OnPlaceSelectedFromResult(val place: Place) : RouteDetailAction()
    data object OnOriginClick : RouteDetailAction()
    data object OnDestinationClick : RouteDetailAction()
    data class OnEditWaypointClick(val index: Int) : RouteDetailAction()
    data object OnAddWaypointClick : RouteDetailAction()
    data object OnCancelSelection : RouteDetailAction()
    data object OnUpdateClick : RouteDetailAction()
    data object OnBackClick : RouteDetailAction()
}
