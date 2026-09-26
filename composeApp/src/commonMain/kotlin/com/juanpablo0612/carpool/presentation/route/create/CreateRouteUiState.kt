package com.juanpablo0612.carpool.presentation.route.create

import com.juanpablo0612.carpool.presentation.place.stops.SelectionTarget
import com.juanpablo0612.carpool.presentation.place.stops.StopsDraft
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

data class CreateRouteUiState(
    val name: String = "",
    val stops: StopsDraft = StopsDraft(),
    val recurringDays: Set<DayOfWeek> = emptySet(),
    val typicalDepartureTime: LocalTime? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val showDiscardConfirm: Boolean = false,
    val error: CreateRouteError? = null,
    val selectionTarget: SelectionTarget? = null
) {
    val isValid: Boolean
        get() = name.isNotBlank() && stops.isComplete

    val isDirty: Boolean
        get() = name.isNotBlank() || !stops.isEmpty || recurringDays.isNotEmpty() || typicalDepartureTime != null
}
