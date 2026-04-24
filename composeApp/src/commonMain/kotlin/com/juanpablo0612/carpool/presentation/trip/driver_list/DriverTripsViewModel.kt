package com.juanpablo0612.carpool.presentation.trip.driver_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.trip.use_case.GetDriverTripsUseCase
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

class DriverTripsViewModel(
    private val getDriverTripsUseCase: GetDriverTripsUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DriverTripsUiState())
    val state: StateFlow<DriverTripsUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<DriverTripsEvent>()
    val events: SharedFlow<DriverTripsEvent> = _events.asSharedFlow()

    init {
        loadTrips()
    }

    private fun loadTrips() {
        val driverId = authRepository.getCurrentUserId() ?: run {
            _state.update { it.copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            getDriverTripsUseCase(driverId)
                .onEach { trips ->
                    val sorted = trips.sortedBy { it.departureTime }
                    _state.update { it.copy(trips = sorted, isLoading = false) }
                }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect {}
        }
    }

    fun onAction(action: DriverTripsAction) {
        when (action) {
            DriverTripsAction.OnBackClick -> viewModelScope.launch {
                _events.emit(DriverTripsEvent.NavigateBack)
            }
        }
    }
}
