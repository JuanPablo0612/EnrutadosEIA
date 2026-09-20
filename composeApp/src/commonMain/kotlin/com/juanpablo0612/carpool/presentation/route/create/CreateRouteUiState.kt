package com.juanpablo0612.carpool.presentation.route.create

import com.juanpablo0612.carpool.domain.place.model.Place
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

data class CreateRouteUiState(
    val name: String = "",
    val origin: Place? = null,
    val destination: Place? = null,
    val waypoints: List<Place> = emptyList(),
    val recurringDays: Set<DayOfWeek> = emptySet(),
    val typicalDepartureTime: LocalTime? = null,
    val isShared: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val showDiscardConfirm: Boolean = false,
    val error: CreateRouteError? = null,
    val selectionTarget: SelectionTarget? = null
) {
    val isValid: Boolean
        get() = name.isNotBlank() && origin != null && destination != null

    val isDirty: Boolean
        get() = name.isNotBlank() || origin != null || destination != null || waypoints.isNotEmpty() ||
            recurringDays.isNotEmpty() || typicalDepartureTime != null || isShared
}

sealed class SelectionTarget {
    data object Origin : SelectionTarget()
    data object Destination : SelectionTarget()
    data class EditWaypoint(val index: Int) : SelectionTarget()
    data object NewWaypoint : SelectionTarget()
}
