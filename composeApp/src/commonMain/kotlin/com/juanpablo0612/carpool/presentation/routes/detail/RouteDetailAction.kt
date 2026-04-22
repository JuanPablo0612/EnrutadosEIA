package com.juanpablo0612.carpool.presentation.routes.detail

import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

sealed class RouteDetailAction {
    data class OnRouteTypeChange(val type: RouteType) : RouteDetailAction()
    data class OnTimeChange(val time: LocalTime) : RouteDetailAction()
    data class OnDayToggled(val day: DayOfWeek) : RouteDetailAction()
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
