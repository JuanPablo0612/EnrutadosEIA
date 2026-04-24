package com.juanpablo0612.carpool.presentation.routes.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.routes.model.Route
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
                            origin = route.origin,
                            destination = route.destination,
                            waypoints = route.waypoints,
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

    private fun onPlaceSelected(place: com.juanpablo0612.carpool.domain.places.model.Place) {
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

        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val route = Route(
                id = routeId,
                driverId = s.driverId,
                origin = origin,
                destination = destination,
                waypoints = s.waypoints
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
