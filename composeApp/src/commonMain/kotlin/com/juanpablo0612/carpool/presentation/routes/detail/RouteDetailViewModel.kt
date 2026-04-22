package com.juanpablo0612.carpool.presentation.routes.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import com.juanpablo0612.carpool.domain.routes.use_case.GetRouteByIdUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.UpdateRouteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RouteDetailViewModel(
    private val routeId: String,
    private val getRouteByIdUseCase: GetRouteByIdUseCase,
    private val updateRouteUseCase: UpdateRouteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RouteDetailUiState())
    val state: StateFlow<RouteDetailUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<RouteDetailEvent>()
    val events: SharedFlow<RouteDetailEvent> = _events.asSharedFlow()

    init {
        loadRoute()
    }

    private fun loadRoute() {
        viewModelScope.launch {
            getRouteByIdUseCase(routeId)
                .onSuccess { route ->
                    _state.update {
                        it.copy(
                            driverId = route.driverId,
                            routeType = route.type,
                            origin = route.origin,
                            destination = route.destination,
                            waypoints = route.waypoints,
                            targetTime = route.targetTime,
                            selectedDays = route.daysOfWeek.toSet(),
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, error = RouteDetailError.RouteNotFound) }
                }
        }
    }

    fun onAction(action: RouteDetailAction) {
        when (action) {
            is RouteDetailAction.OnRouteTypeChange -> onRouteTypeChange(action.type)
            is RouteDetailAction.OnTimeChange -> _state.update { it.copy(targetTime = action.time) }
            is RouteDetailAction.OnDayToggled -> onDayToggled(action.day)
            is RouteDetailAction.OnRemoveWaypoint -> _state.update {
                it.copy(waypoints = it.waypoints.filterIndexed { i, _ -> i != action.index })
            }
            RouteDetailAction.OnOriginClick -> _state.update {
                it.copy(selectionTarget = RouteDetailSelectionTarget.Origin)
            }
            RouteDetailAction.OnDestinationClick -> _state.update {
                it.copy(selectionTarget = RouteDetailSelectionTarget.Destination)
            }
            is RouteDetailAction.OnEditWaypointClick -> _state.update {
                it.copy(selectionTarget = RouteDetailSelectionTarget.EditWaypoint(action.index))
            }
            RouteDetailAction.OnAddWaypointClick -> _state.update {
                it.copy(selectionTarget = RouteDetailSelectionTarget.NewWaypoint)
            }
            is RouteDetailAction.OnPlaceSelectedFromResult -> onPlaceSelected(action.place)
            RouteDetailAction.OnCancelSelection -> _state.update { it.copy(selectionTarget = null) }
            RouteDetailAction.OnUpdateClick -> updateRoute()
            RouteDetailAction.OnBackClick -> viewModelScope.launch {
                _events.emit(RouteDetailEvent.NavigateBack)
            }
        }
    }

    private fun onRouteTypeChange(type: RouteType) {
        _state.update {
            it.copy(
                routeType = type,
                origin = if (type is RouteType.FromUniversity) Place.UNIVERSITY_EIA else it.origin,
                destination = if (type is RouteType.ToUniversity) Place.UNIVERSITY_EIA else it.destination
            )
        }
    }

    private fun onDayToggled(day: kotlinx.datetime.DayOfWeek) {
        _state.update {
            val newDays = if (it.selectedDays.contains(day)) it.selectedDays - day else it.selectedDays + day
            it.copy(selectedDays = newDays)
        }
    }

    private fun onPlaceSelected(place: Place) {
        val target = _state.value.selectionTarget ?: return
        _state.update {
            when (target) {
                RouteDetailSelectionTarget.Origin -> it.copy(origin = place, selectionTarget = null)
                RouteDetailSelectionTarget.Destination -> it.copy(destination = place, selectionTarget = null)
                is RouteDetailSelectionTarget.EditWaypoint -> {
                    val updated = it.waypoints.toMutableList()
                    updated[target.index] = place
                    it.copy(waypoints = updated, selectionTarget = null)
                }
                RouteDetailSelectionTarget.NewWaypoint -> it.copy(
                    waypoints = it.waypoints + place,
                    selectionTarget = null
                )
            }
        }
    }

    private fun updateRoute() {
        val s = _state.value
        val origin = s.origin
        val destination = s.destination

        if (origin == null || destination == null) {
            _state.update { it.copy(error = RouteDetailError.OriginDestinationRequired) }
            return
        }
        if (s.waypoints.isEmpty()) {
            _state.update { it.copy(error = RouteDetailError.AtLeastOneWaypoint) }
            return
        }
        if (s.selectedDays.isEmpty()) {
            _state.update { it.copy(error = RouteDetailError.AtLeastOneDay) }
            return
        }

        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val route = Route(
                id = routeId,
                driverId = s.driverId,
                origin = origin,
                destination = destination,
                waypoints = s.waypoints,
                targetTime = s.targetTime,
                daysOfWeek = s.selectedDays.toList(),
                type = s.routeType
            )
            updateRouteUseCase(route)
                .onSuccess {
                    _state.update { it.copy(isSaving = false) }
                    _events.emit(RouteDetailEvent.RouteUpdated)
                }
                .onFailure {
                    _state.update { it.copy(isSaving = false, error = RouteDetailError.Unknown) }
                }
        }
    }
}
