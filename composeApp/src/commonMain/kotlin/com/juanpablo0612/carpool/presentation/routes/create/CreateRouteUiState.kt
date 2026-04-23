package com.juanpablo0612.carpool.presentation.routes.create

import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

data class CreateRouteUiState(
    val routeType: RouteType = RouteType.ToUniversity,
    val origin: Place? = null,
    val destination: Place? = Place.UNIVERSITY_EIA,
    val waypoints: List<Place> = emptyList(),
    val targetTime: LocalTime = LocalTime(7, 0),
    val selectedDays: Set<DayOfWeek> = emptySet(),
    val isLoading: Boolean = false,
    val error: CreateRouteError? = null,
    val selectionTarget: SelectionTarget? = null
)

sealed class SelectionTarget {
    data object Origin : SelectionTarget()
    data object Destination : SelectionTarget()
    data class EditWaypoint(val index: Int) : SelectionTarget()
    data object NewWaypoint : SelectionTarget()
}
