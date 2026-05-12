package com.juanpablo0612.carpool.presentation.vehicles.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.use_case.GetDriverTripsUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.DeleteVehicleUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetUserVehiclesUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.SetPrimaryVehicleUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehiclesListViewModel(
    private val getUserVehiclesUseCase: GetUserVehiclesUseCase,
    private val authRepository: AuthRepository,
    private val deleteVehicleUseCase: DeleteVehicleUseCase,
    private val setPrimaryVehicleUseCase: SetPrimaryVehicleUseCase,
    private val getDriverTripsUseCase: GetDriverTripsUseCase,
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
            is VehiclesListAction.OnEditVehicle -> viewModelScope.launch {
                _events.emit(VehiclesListEvent.NavigateToEditVehicle(action.vehicleId))
            }

            is VehiclesListAction.OnSetPrimary -> {
                val userId = authRepository.getCurrentUserId() ?: return
                viewModelScope.launch {
                    setPrimaryVehicleUseCase(userId, action.vehicleId)
                }
            }

            is VehiclesListAction.OnDeleteRequest -> {
                val driverId = authRepository.getCurrentUserId() ?: return
                viewModelScope.launch {
                    val activeTrips = getDriverTripsUseCase(driverId).first()
                        .filter { it.vehicleId == action.vehicle.id && it.status == TripStatus.Active }
                    if (activeTrips.isNotEmpty()) {
                        _state.update { it.copy(deleteBlockedVehicle = action.vehicle) }
                    } else {
                        _state.update { it.copy(vehicleToDelete = action.vehicle) }
                    }
                }
            }

            VehiclesListAction.OnConfirmDelete -> {
                val vehicle = _state.value.vehicleToDelete ?: return
                _state.update { it.copy(vehicleToDelete = null) }
                viewModelScope.launch {
                    deleteVehicleUseCase(vehicle.id, vehicle.driverId)
                }
            }

            VehiclesListAction.OnDismissDeleteDialog ->
                _state.update { it.copy(vehicleToDelete = null) }

            VehiclesListAction.OnDismissBlockedDialog ->
                _state.update { it.copy(deleteBlockedVehicle = null) }

            VehiclesListAction.OnAddVehicle -> viewModelScope.launch {
                _events.emit(VehiclesListEvent.NavigateToRegisterVehicle)
            }

            VehiclesListAction.OnBackClick -> viewModelScope.launch {
                _events.emit(VehiclesListEvent.NavigateBack)
            }
        }
    }
}
