package com.juanpablo0612.carpool.presentation.place.stops

import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.route.model.Route

/** The stops of a route or trip being edited: origin, optional waypoints, destination. */
data class StopsDraft(
    val origin: Place? = null,
    val destination: Place? = null,
    val waypoints: List<Place> = emptyList(),
) {
    val isComplete: Boolean get() = origin != null && destination != null

    val isEmpty: Boolean get() = origin == null && destination == null && waypoints.isEmpty()

    /** Puts [place] where [target] says. */
    fun apply(target: SelectionTarget, place: Place): StopsDraft = when (target) {
        SelectionTarget.Origin -> copy(origin = place)
        SelectionTarget.Destination -> copy(destination = place)
        is SelectionTarget.EditWaypoint -> copy(
            waypoints = waypoints.mapIndexed { i, existing -> if (i == target.index) place else existing }
        )
        SelectionTarget.NewWaypoint -> copy(waypoints = waypoints + place)
    }

    fun removeWaypoint(index: Int): StopsDraft = copy(waypoints = waypoints.filterIndexed { i, _ -> i != index })

    /** The return trip: destination becomes origin and the stops run the other way. */
    fun reversed(): StopsDraft = StopsDraft(origin = destination, destination = origin, waypoints = waypoints.reversed())

    companion object {
        fun of(route: Route) = StopsDraft(route.origin, route.destination, route.waypoints)
    }
}

/** Which stop the place selector is currently choosing. */
sealed class SelectionTarget {
    data object Origin : SelectionTarget()
    data object Destination : SelectionTarget()
    data class EditWaypoint(val index: Int) : SelectionTarget()
    data object NewWaypoint : SelectionTarget()
}
