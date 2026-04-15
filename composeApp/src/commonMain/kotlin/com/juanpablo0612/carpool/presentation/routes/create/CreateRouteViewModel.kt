package com.juanpablo0612.carpool.presentation.routes.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.data.places.model.PlaceDto
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import com.juanpablo0612.carpool.domain.routes.use_case.CreateRouteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class CreateRouteViewModel(
    private val createRouteUseCase: CreateRouteUseCase,
    private val authRepository: AuthRepository // To get current user ID
) : ViewModel() {

    private val _state = MutableStateFlow(CreateRouteUiState())
    val state: StateFlow<CreateRouteUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CreateRouteEvent>()
    val events: SharedFlow<CreateRouteEvent> = _events.asSharedFlow()

    fun onAction(action: CreateRouteAction) {
        when (action) {
            is CreateRouteAction.OnRouteTypeChange -> {
                _state.update {
                    it.copy(
                        routeType = action.type,
                        origin = if (action.type is RouteType.FromUniversity) Place.UNIVERSITY_EIA else null,
                        destination = if (action.type is RouteType.ToUniversity) Place.UNIVERSITY_EIA else null
                    )
                }
            }
            is CreateRouteAction.OnOriginChange -> {
                if (_state.value.routeType is RouteType.ToUniversity) {
                    _state.update { it.copy(origin = action.place) }
                }
            }
            is CreateRouteAction.OnDestinationChange -> {
                if (_state.value.routeType is RouteType.FromUniversity) {
                    _state.update { it.copy(destination = action.place) }
                }
            }
            is CreateRouteAction.OnAddWaypoint -> {
                _state.update { it.copy(waypoints = it.waypoints + action.place) }
            }
            is CreateRouteAction.OnRemoveWaypoint -> {
                _state.update {
                    it.copy(waypoints = it.waypoints.filterIndexed { index, _ -> index != action.index })
                }
            }
            is CreateRouteAction.OnTimeChange -> {
                _state.update { it.copy(targetTime = action.time) }
            }
            is CreateRouteAction.OnDayToggled -> {
                _state.update {
                    val newDays = if (it.selectedDays.contains(action.day)) {
                        it.selectedDays - action.day
                    } else {
                        it.selectedDays + action.day
                    }
                    it.copy(selectedDays = newDays)
                }
            }
            CreateRouteAction.OnSaveClick -> createRoute()
            CreateRouteAction.OnBackClick -> {
                viewModelScope.launch { _events.emit(CreateRouteEvent.NavigateBack) }
            }
            CreateRouteAction.OnCancelSelection -> {
                _state.update { it.copy(selectionTarget = null) }
            }
            is CreateRouteAction.OnWaypointClick -> {
                val index = action.index
                _state.update {
                    val target = when (index) {
                        -1 -> SelectionTarget.Origin
                        -2 -> SelectionTarget.Destination
                        null -> null
                        else -> SelectionTarget.Waypoint(index)
                    }
                    it.copy(selectionTarget = target)
                }
            }
            is CreateRouteAction.OnPlaceSelectedFromResult -> {
                val target = _state.value.selectionTarget
                when (target) {
                    SelectionTarget.Origin -> {
                        _state.update { it.copy(origin = action.place, selectionTarget = null) }
                    }
                    SelectionTarget.Destination -> {
                        _state.update { it.copy(destination = action.place, selectionTarget = null) }
                    }
                    is SelectionTarget.Waypoint -> {
                        _state.update {
                            val newWaypoints = it.waypoints.toMutableList()
                            if (target.index < newWaypoints.size) {
                                newWaypoints[target.index] = action.place
                            } else {
                                newWaypoints.add(action.place)
                            }
                            it.copy(waypoints = newWaypoints, selectionTarget = null)
                        }
                    }
                    null -> {}
                }
            }
        }
    }

    private fun createRoute() {
        val currentState = _state.value
        val origin = currentState.origin
        val destination = currentState.destination
        
        if (origin == null || destination == null) {
            viewModelScope.launch { _events.emit(CreateRouteEvent.ShowError(CreateRouteError.OriginDestinationRequired)) }
            return
        }

        if (currentState.waypoints.isEmpty()) {
            viewModelScope.launch { _events.emit(CreateRouteEvent.ShowError(CreateRouteError.AtLeastOneWaypoint)) }
            return
        }

        if (currentState.selectedDays.isEmpty()) {
            viewModelScope.launch { _events.emit(CreateRouteEvent.ShowError(CreateRouteError.AtLeastOneDay)) }
            return
        }

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            viewModelScope.launch { _events.emit(CreateRouteEvent.ShowError(CreateRouteError.UserNotAuthenticated)) }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val route = Route(
                driverId = userId,
                origin = origin,
                destination = destination,
                waypoints = currentState.waypoints,
                targetTime = currentState.targetTime,
                daysOfWeek = currentState.selectedDays.toList(),
                type = currentState.routeType
            )

            createRouteUseCase(route)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                    _events.emit(CreateRouteEvent.RouteCreated)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = CreateRouteError.Unknown) }
                    _events.emit(CreateRouteEvent.ShowError(CreateRouteError.Unknown))
                }
        }
    }
}
