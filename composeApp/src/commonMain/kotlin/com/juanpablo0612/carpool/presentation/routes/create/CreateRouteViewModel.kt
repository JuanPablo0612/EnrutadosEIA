package com.juanpablo0612.carpool.presentation.routes.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.routes.model.Route
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
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateRouteUiState())
    val state: StateFlow<CreateRouteUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CreateRouteEvent>()
    val events: SharedFlow<CreateRouteEvent> = _events.asSharedFlow()

    fun onAction(action: CreateRouteAction) {
        when (action) {
            is CreateRouteAction.OnRemoveWaypoint -> _state.update {
                it.copy(waypoints = it.waypoints.filterIndexed { i, _ -> i != action.index })
            }
            CreateRouteAction.OnOriginClick -> _state.update {
                it.copy(selectionTarget = SelectionTarget.Origin)
            }
            CreateRouteAction.OnDestinationClick -> _state.update {
                it.copy(selectionTarget = SelectionTarget.Destination)
            }
            is CreateRouteAction.OnEditWaypointClick -> _state.update {
                it.copy(selectionTarget = SelectionTarget.EditWaypoint(action.index))
            }
            CreateRouteAction.OnAddWaypointClick -> _state.update {
                it.copy(selectionTarget = SelectionTarget.NewWaypoint)
            }
            is CreateRouteAction.OnPlaceSelectedFromResult -> onPlaceSelected(action)
            CreateRouteAction.OnCancelSelection -> _state.update { it.copy(selectionTarget = null) }
            CreateRouteAction.OnSaveClick -> createRoute()
            CreateRouteAction.OnBackClick -> viewModelScope.launch {
                _events.emit(CreateRouteEvent.NavigateBack)
            }
        }
    }

    private fun onPlaceSelected(action: CreateRouteAction.OnPlaceSelectedFromResult) {
        val target = _state.value.selectionTarget ?: return
        _state.update {
            when (target) {
                SelectionTarget.Origin -> it.copy(origin = action.place, selectionTarget = null)
                SelectionTarget.Destination -> it.copy(destination = action.place, selectionTarget = null)
                is SelectionTarget.EditWaypoint -> {
                    val updated = it.waypoints.toMutableList()
                    updated[target.index] = action.place
                    it.copy(waypoints = updated, selectionTarget = null)
                }
                SelectionTarget.NewWaypoint -> it.copy(
                    waypoints = it.waypoints + action.place,
                    selectionTarget = null
                )
            }
        }
    }

    private fun createRoute() {
        val currentState = _state.value
        val origin = currentState.origin
        val destination = currentState.destination

        if (origin == null || destination == null) {
            _state.update { it.copy(error = CreateRouteError.OriginDestinationRequired) }
            return
        }
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            _state.update { it.copy(error = CreateRouteError.UserNotAuthenticated) }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val route = Route(
                driverId = userId,
                origin = origin,
                destination = destination,
                waypoints = currentState.waypoints
            )
            createRouteUseCase(route)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.emit(CreateRouteEvent.RouteCreated)
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, error = CreateRouteError.Unknown) }
                }
        }
    }
}
