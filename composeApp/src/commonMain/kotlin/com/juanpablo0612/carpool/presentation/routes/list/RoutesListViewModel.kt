package com.juanpablo0612.carpool.presentation.routes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.routes.use_case.GetUserRoutesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoutesListViewModel(
    private val getUserRoutesUseCase: GetUserRoutesUseCase,
    private val authRepository: AuthRepository
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
            getUserRoutesUseCase(userId)
                .onEach { routes -> _state.update { it.copy(routes = routes, isLoading = false) } }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect {}
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
        }
    }
}
