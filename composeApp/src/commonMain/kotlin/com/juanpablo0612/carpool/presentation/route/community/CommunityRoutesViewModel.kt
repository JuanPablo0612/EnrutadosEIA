package com.juanpablo0612.carpool.presentation.route.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository
import com.juanpablo0612.carpool.domain.route.usecase.DuplicateRouteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommunityRoutesViewModel(
    private val routeRepository: RouteRepository,
    private val authRepository: AuthRepository,
    private val duplicateRouteUseCase: DuplicateRouteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CommunityRoutesUiState())
    val state: StateFlow<CommunityRoutesUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CommunityRoutesEvent>()
    val events: SharedFlow<CommunityRoutesEvent> = _events.asSharedFlow()

    private var routesJob: Job? = null

    init {
        loadRoutes()
    }

    // Cancel any previous subscription first — otherwise a retry/refresh stacks a second live
    // Firestore listener on top of the first instead of replacing it.
    private fun loadRoutes(isRefresh: Boolean = false) {
        routesJob?.cancel()
        _state.update { if (isRefresh) it.copy(isRefreshing = true) else it.copy(isLoading = true, error = null) }
        val currentUserId = authRepository.getCurrentUserId()
        routesJob = viewModelScope.launch {
            routeRepository.getCommunityRoutes()
                .map { routes -> routes.filter { it.driverId != currentUserId } }
                .catch { _state.update { it.copy(isLoading = false, isRefreshing = false, error = CommunityRoutesError.LoadFailed) } }
                .collect { routes ->
                    _state.update { it.copy(routes = routes, isLoading = false, isRefreshing = false, error = null) }
                }
        }
    }

    fun onAction(action: CommunityRoutesAction) {
        when (action) {
            is CommunityRoutesAction.OnReuseClick -> reuseRoute(action.routeId)
            CommunityRoutesAction.OnRetry -> loadRoutes()
            CommunityRoutesAction.Refresh -> loadRoutes(isRefresh = true)
            CommunityRoutesAction.OnDismissActionError -> _state.update { it.copy(actionError = null) }
            CommunityRoutesAction.OnBackClick -> viewModelScope.launch {
                _events.emit(CommunityRoutesEvent.NavigateBack)
            }
        }
    }

    private fun reuseRoute(routeId: String) {
        val route = _state.value.routes.find { it.id == routeId } ?: return
        _state.update { it.copy(reusingRouteId = routeId, actionError = null) }
        viewModelScope.launch {
            duplicateRouteUseCase(route)
                .onSuccess {
                    _state.update { it.copy(reusingRouteId = null) }
                    _events.emit(CommunityRoutesEvent.NavigateBack)
                }
                .onFailure {
                    _state.update { it.copy(reusingRouteId = null, actionError = CommunityRoutesError.ReuseFailed) }
                }
        }
    }
}
