package com.juanpablo0612.carpool.presentation.vehicles.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetUserVehiclesUseCase
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

class VehiclesListViewModel(
    private val getUserVehiclesUseCase: GetUserVehiclesUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VehiclesListUiState())
    val state: StateFlow<VehiclesListUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<VehiclesListEvent>()
    val events: SharedFlow<VehiclesListEvent> = _events.asSharedFlow()

    init {
        loadVehicles()
    }

    private fun loadVehicles() {
        val userId = authRepository.getCurrentUserId() ?: run {
            _state.update { it.copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            getUserVehiclesUseCase(userId)
                .onEach { vehicles -> _state.update { it.copy(vehicles = vehicles, isLoading = false) } }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect {}
        }
    }

    fun onAction(action: VehiclesListAction) {
        when (action) {
            VehiclesListAction.OnRegisterVehicleClick -> viewModelScope.launch {
                _events.emit(VehiclesListEvent.NavigateToRegisterVehicle)
            }
        }
    }
}
