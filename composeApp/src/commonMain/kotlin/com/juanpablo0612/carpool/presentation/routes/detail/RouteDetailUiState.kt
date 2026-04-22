package com.juanpablo0612.carpool.presentation.routes.detail

import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

data class RouteDetailUiState(
    val driverId: String = "",
    val routeType: RouteType = RouteType.ToUniversity,
    val origin: Place? = null,
    val destination: Place? = null,
    val waypoints: List<Place> = emptyList(),
    val targetTime: LocalTime = LocalTime(7, 0),
    val selectedDays: Set<DayOfWeek> = emptySet(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: RouteDetailError? = null,
    val selectionTarget: RouteDetailSelectionTarget? = null
)

sealed class RouteDetailSelectionTarget {
    data object Origin : RouteDetailSelectionTarget()
    data object Destination : RouteDetailSelectionTarget()
    data class EditWaypoint(val index: Int) : RouteDetailSelectionTarget()
    data object NewWaypoint : RouteDetailSelectionTarget()
}
