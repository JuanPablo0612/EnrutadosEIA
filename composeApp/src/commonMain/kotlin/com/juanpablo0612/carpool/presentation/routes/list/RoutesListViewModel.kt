package com.juanpablo0612.carpool.presentation.routes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.routes.use_case.DeleteRouteUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.GetUserRoutesUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.GetDriverTripsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class RoutesListViewModel(
    private val getUserRoutesUseCase: GetUserRoutesUseCase,
    private val getDriverTripsUseCase: GetDriverTripsUseCase,
    private val authRepository: AuthRepository,
    private val deleteRouteUseCase: DeleteRouteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RoutesListUiState())
    val state: StateFlow<RoutesListUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<RoutesListEvent>()
    val events: SharedFlow<RoutesListEvent> = _events.asSharedFlow()

    init {
        loadRoutes()
    }

    private fun loadRoutes() {
        val userId = authRepository.getCurrentUserId() ?: run {
            _state.update { it.copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            combine(
                getUserRoutesUseCase(userId),
                getDriverTripsUseCase(userId)
            ) { routes, trips ->
                routes.map { route ->
                    val routeTrips = trips.filter { it.routeId == route.id }
                    RouteWithStats(
                        route = route,
                        tripsCount = routeTrips.size,
                        lastUsedAt = routeTrips.maxOfOrNull { it.departureTime }
                            ?.let { Instant.fromEpochMilliseconds(it) }
                    )
                }
            }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect { routesWithStats ->
                    _state.update { it.copy(routes = routesWithStats, isLoading = false) }
                }
        }
    }

    fun onAction(action: RoutesListAction) {
        when (action) {
            RoutesListAction.OnCreateRouteClick -> viewModelScope.launch {
                _events.emit(RoutesListEvent.NavigateToCreateRoute)
            }
            is RoutesListAction.OnRouteClick -> viewModelScope.launch {
                _events.emit(RoutesListEvent.NavigateToRouteDetail(action.routeId))
            }
            is RoutesListAction.OnPublishTripClick -> viewModelScope.launch {
                _events.emit(RoutesListEvent.NavigateToCreateTrip(action.routeId))
            }
            is RoutesListAction.OnDeleteRouteClick -> {
                _state.update { it.copy(pendingDeleteRouteId = action.routeId) }
            }
            is RoutesListAction.OnDuplicateRouteClick -> viewModelScope.launch {
                _events.emit(RoutesListEvent.NavigateToRouteDetail(action.routeId))
            }
            RoutesListAction.OnConfirmDelete -> deleteRoute()
            RoutesListAction.OnDismissDelete -> {
                _state.update { it.copy(pendingDeleteRouteId = null) }
            }
            RoutesListAction.OnBackClick -> viewModelScope.launch {
                _events.emit(RoutesListEvent.NavigateBack)
            }
        }
    }

    private fun deleteRoute() {
        val routeId = _state.value.pendingDeleteRouteId ?: return
        _state.update { it.copy(pendingDeleteRouteId = null) }
        viewModelScope.launch {
            deleteRouteUseCase(routeId)
        }
    }
}
