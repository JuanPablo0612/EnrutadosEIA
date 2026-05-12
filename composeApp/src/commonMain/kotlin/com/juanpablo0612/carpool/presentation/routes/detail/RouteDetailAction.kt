package com.juanpablo0612.carpool.presentation.routes.detail

import com.juanpablo0612.carpool.domain.places.model.Place
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

sealed class RouteDetailAction {
    // Toolbar actions
    data object OnBackClick : RouteDetailAction()
    data object OnEditClick : RouteDetailAction()
    data object OnCancelEdit : RouteDetailAction()
    data object OnSaveChangesClick : RouteDetailAction()
    data object OnDeleteClick : RouteDetailAction()
    data object OnConfirmDelete : RouteDetailAction()
    data object OnDismissDelete : RouteDetailAction()
    data object OnPublishTripClick : RouteDetailAction()
    data object OnDuplicateClick : RouteDetailAction()

    // Draft editing (mirrors CreateRouteAction)
    data class OnNameChange(val name: String) : RouteDetailAction()
    data class OnToggleRecurringDay(val day: DayOfWeek) : RouteDetailAction()
    data class OnSetDepartureTime(val time: LocalTime?) : RouteDetailAction()
    data object OnOriginClick : RouteDetailAction()
    data object OnDestinationClick : RouteDetailAction()
    data class OnEditWaypointClick(val index: Int) : RouteDetailAction()
    data object OnAddWaypointClick : RouteDetailAction()
    data class OnRemoveWaypoint(val index: Int) : RouteDetailAction()
    data class OnPlaceSelectedFromResult(val place: Place) : RouteDetailAction()
    data object OnCancelSelection : RouteDetailAction()
}
