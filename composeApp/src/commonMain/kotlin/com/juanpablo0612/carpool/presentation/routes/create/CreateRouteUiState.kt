package com.juanpablo0612.carpool.presentation.routes.create

import com.juanpablo0612.carpool.domain.places.model.Place

data class CreateRouteUiState(
    val origin: Place? = null,
    val destination: Place? = null,
    val waypoints: List<Place> = emptyList(),
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
