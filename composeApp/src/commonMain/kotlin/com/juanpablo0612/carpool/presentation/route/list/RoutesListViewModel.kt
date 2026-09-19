package com.juanpablo0612.carpool.presentation.route.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository
import com.juanpablo0612.carpool.domain.route.usecase.DuplicateRouteUseCase
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
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
    private val routeRepository: RouteRepository,
    private val tripRepository: TripRepository,
    private val authRepository: AuthRepository,
    private val duplicateRouteUseCase: DuplicateRouteUseCase
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
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            combine(
                routeRepository.getUserRoutes(userId),
                tripRepository.getDriverTrips(userId)
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
                .catch { _state.update { it.copy(isLoading = false, error = RoutesListError.LoadFailed) } }
                .collect { routesWithStats ->
                    _state.update { it.copy(routes = routesWithStats, isLoading = false, error = null) }
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
            is RoutesListAction.OnDuplicateRouteClick -> duplicateRoute(action.routeId)
            RoutesListAction.OnConfirmDelete -> deleteRoute()
            RoutesListAction.OnDismissDelete -> {
                _state.update { it.copy(pendingDeleteRouteId = null) }
            }
            RoutesListAction.OnRetry -> loadRoutes()
            RoutesListAction.OnDismissActionError -> _state.update { it.copy(actionError = null) }
            RoutesListAction.OnBackClick -> viewModelScope.launch {
                _events.emit(RoutesListEvent.NavigateBack)
            }
            RoutesListAction.OnCommunityRoutesClick -> viewModelScope.launch {
                _events.emit(RoutesListEvent.NavigateToCommunityRoutes)
            }
        }
    }

    // Mirrors RouteDetailViewModel.duplicateRoute() — the user is already on the list (which is
    // Flow-driven off a live Firestore listener), so unlike the detail screen's version there's
    // no NavigateBack: the duplicate just appears once routeRepository.getUserRoutes() re-emits.
    private fun duplicateRoute(routeId: String) {
        val route = _state.value.routes.find { it.route.id == routeId }?.route ?: return
        _state.update { it.copy(duplicatingRouteId = routeId, actionError = null) }
        viewModelScope.launch {
            duplicateRouteUseCase(route, nameOverride = "${route.name} (copia)")
                .onSuccess {
                    _state.update { it.copy(duplicatingRouteId = null) }
                }
                .onFailure {
                    _state.update {
                        it.copy(duplicatingRouteId = null, actionError = RoutesListError.DuplicateFailed)
                    }
                }
        }
    }

    private fun deleteRoute() {
        val routeId = _state.value.pendingDeleteRouteId ?: return
        _state.update { it.copy(pendingDeleteRouteId = null) }
        viewModelScope.launch {
            routeRepository.deleteRoute(routeId)
        }
    }
}
