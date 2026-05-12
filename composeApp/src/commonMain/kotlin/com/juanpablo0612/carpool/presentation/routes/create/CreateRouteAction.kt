package com.juanpablo0612.carpool.presentation.routes.create

import com.juanpablo0612.carpool.domain.places.model.Place
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

sealed class CreateRouteAction {
    data class OnNameChange(val name: String) : CreateRouteAction()
    data class OnRemoveWaypoint(val index: Int) : CreateRouteAction()
    data class OnPlaceSelectedFromResult(val place: Place) : CreateRouteAction()
    data class OnEditWaypointClick(val index: Int) : CreateRouteAction()
    data class OnToggleRecurringDay(val day: DayOfWeek) : CreateRouteAction()
    data class OnSetDepartureTime(val time: LocalTime?) : CreateRouteAction()
    data object OnOriginClick : CreateRouteAction()
    data object OnDestinationClick : CreateRouteAction()
    data object OnAddWaypointClick : CreateRouteAction()
    data object OnCancelSelection : CreateRouteAction()
    data object OnSaveClick : CreateRouteAction()
    data object OnBackClick : CreateRouteAction()
}
