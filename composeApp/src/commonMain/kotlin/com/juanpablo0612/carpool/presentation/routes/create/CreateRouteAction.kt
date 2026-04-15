package com.juanpablo0612.carpool.presentation.routes.create

import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

sealed class CreateRouteAction {
    data class OnRouteTypeChange(val type: RouteType) : CreateRouteAction()
    data class OnOriginChange(val place: Place) : CreateRouteAction()
    data class OnDestinationChange(val place: Place) : CreateRouteAction()
    data class OnAddWaypoint(val place: Place) : CreateRouteAction()
    data class OnRemoveWaypoint(val index: Int) : CreateRouteAction()
    data class OnTimeChange(val time: LocalTime) : CreateRouteAction()
    data class OnDayToggled(val day: DayOfWeek) : CreateRouteAction()
    data object OnSaveClick : CreateRouteAction()
    data object OnBackClick : CreateRouteAction()
    data class OnWaypointClick(val index: Int?) : CreateRouteAction()
    data class OnPlaceSelectedFromResult(val place: Place) : CreateRouteAction()
    data object OnCancelSelection : CreateRouteAction()
}
