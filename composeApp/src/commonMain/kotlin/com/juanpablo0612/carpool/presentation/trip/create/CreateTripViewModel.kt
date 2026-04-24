package com.juanpablo0612.carpool.presentation.trip.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.routes.use_case.GetRouteByIdUseCase
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripError
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.use_case.CreateTripUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetUserVehiclesUseCase
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
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class CreateTripViewModel(
    private val routeId: String,
    private val getRouteByIdUseCase: GetRouteByIdUseCase,
    private val getUserVehiclesUseCase: GetUserVehiclesUseCase,
    private val createTripUseCase: CreateTripUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateTripUiState())
    val state: StateFlow<CreateTripUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CreateTripEvent>()
    val events: SharedFlow<CreateTripEvent> = _events.asSharedFlow()

    init {
        val nowMs = Clock.System.now().toEpochMilliseconds()
        val now = Instant.fromEpochMilliseconds(nowMs).toLocalDateTime(TimeZone.currentSystemDefault())
        _state.update { it.copy(departureDate = now.date, departureTime = now.time) }
        loadRoute()
        loadVehicles()
    }

    private fun loadRoute() {
        viewModelScope.launch {
            getRouteByIdUseCase(routeId)
                .onSuccess { route ->
                    _state.update { it.copy(route = route, isLoading = false) }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, error = TripError.Unknown) }
                }
        }
    }

    private fun loadVehicles() {
        val userId = authRepository.getCurrentUserId() ?: return
        getUserVehiclesUseCase(userId)
            .onEach { vehicles ->
                _state.update { it.copy(vehicles = vehicles) }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: CreateTripAction) {
        when (action) {
            is CreateTripAction.OnVehicleSelected -> _state.update {
                it.copy(selectedVehicleId = action.vehicleId)
            }
            is CreateTripAction.OnDateSelected -> _state.update {
                it.copy(departureDate = action.date, showDatePicker = false)
            }
            is CreateTripAction.OnTimeSelected -> _state.update {
                it.copy(departureTime = action.time, showTimePicker = false)
            }
            CreateTripAction.OnShowDatePicker -> _state.update { it.copy(showDatePicker = true) }
            CreateTripAction.OnShowTimePicker -> _state.update { it.copy(showTimePicker = true) }
            CreateTripAction.OnDismissDatePicker -> _state.update { it.copy(showDatePicker = false) }
            CreateTripAction.OnDismissTimePicker -> _state.update { it.copy(showTimePicker = false) }
            CreateTripAction.OnPublishClick -> publishTrip()
            CreateTripAction.OnBackClick -> viewModelScope.launch {
                _events.emit(CreateTripEvent.NavigateBack)
            }
        }
    }

    private fun publishTrip() {
        val s = _state.value
        val route = s.route ?: return
        val vehicleId = s.selectedVehicleId ?: run {
            _state.update { it.copy(error = TripError.NoVehicleSelected) }
            return
        }
        val date = s.departureDate ?: return
        val time = s.departureTime ?: return
        val driverId = authRepository.getCurrentUserId() ?: run {
            _state.update { it.copy(error = TripError.UserNotAuthenticated) }
            return
        }

        val departureMs = LocalDateTime(date, time)
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()

        _state.update { it.copy(isPublishing = true, error = null) }
        viewModelScope.launch {
            val trip = Trip(
                routeId = routeId,
                driverId = driverId,
                vehicleId = vehicleId,
                origin = route.origin,
                destination = route.destination,
                waypoints = route.waypoints,
                departureTime = departureMs,
                status = TripStatus.Active
            )
            createTripUseCase(trip)
                .onSuccess {
                    _state.update { it.copy(isPublishing = false) }
                    _events.emit(CreateTripEvent.TripPublished)
                }
                .onFailure {
                    _state.update { it.copy(isPublishing = false, error = TripError.Unknown) }
                }
        }
    }
}
