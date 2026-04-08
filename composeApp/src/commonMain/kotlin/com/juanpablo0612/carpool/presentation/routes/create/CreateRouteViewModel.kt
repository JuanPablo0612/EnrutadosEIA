package com.juanpablo0612.carpool.presentation.routes.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        }
    }

    private fun createRoute() {
        val currentState = _state.value
        val origin = currentState.origin
        val destination = currentState.destination
        
        if (origin == null || destination == null) {
            viewModelScope.launch { _events.emit(CreateRouteEvent.ShowError("Origin and destination are required")) }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val route = Route(
                driverId = "TODO_GET_CURRENT_USER_ID", // This needs to be fetched from AuthRepository
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
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                    _events.emit(CreateRouteEvent.ShowError(error.message ?: "Unknown error"))
                }
        }
    }
}
