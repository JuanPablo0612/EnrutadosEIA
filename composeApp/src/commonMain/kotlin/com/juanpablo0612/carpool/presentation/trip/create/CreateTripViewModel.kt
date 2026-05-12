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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
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
        loadRoute()
        loadVehicles()
    }

    private fun loadRoute() {
        viewModelScope.launch {
            getRouteByIdUseCase(routeId)
                .onSuccess { route ->
                    val defaultTime = route.typicalDepartureTime ?: _state.value.departureTime
                    _state.update { it.copy(route = route, departureTime = defaultTime, isLoading = false) }
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
                _state.update { state ->
                    val autoSelected = if (state.selectedVehicleId == null && vehicles.isNotEmpty()) {
                        vehicles.first().id
                    } else {
                        state.selectedVehicleId
                    }
                    val autoSeats = vehicles.find { it.id == autoSelected }?.seatsAvailable
                        ?: state.seatCount
                    state.copy(
                        vehicles = vehicles,
                        selectedVehicleId = autoSelected,
                        seatCount = autoSeats
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: CreateTripAction) {
        when (action) {
            is CreateTripAction.OnVehicleSelected -> {
                val vehicle = _state.value.vehicles.find { it.id == action.vehicleId }
                _state.update {
                    it.copy(
                        selectedVehicleId = action.vehicleId,
                        seatCount = vehicle?.seatsAvailable ?: it.seatCount
                    )
                }
            }
            CreateTripAction.OnSelectTodayDate -> {
                val today = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                _state.update { it.copy(departureDate = today, showDatePicker = false) }
            }
            CreateTripAction.OnSelectTomorrowDate -> {
                val tomorrow = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    .plus(1, DateTimeUnit.DAY)
                _state.update { it.copy(departureDate = tomorrow, showDatePicker = false) }
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
            is CreateTripAction.OnSetSeats -> _state.update { it.copy(seatCount = action.count) }
            is CreateTripAction.OnSetContribution -> _state.update {
                it.copy(contributionPerPassenger = action.pesos)
            }
            is CreateTripAction.OnSetMessage -> _state.update {
                it.copy(messageToPassengers = action.text.take(140))
            }
            CreateTripAction.OnPublishClick -> publishTrip()
            CreateTripAction.OnNavigateToRegisterVehicle -> viewModelScope.launch {
                _events.emit(CreateTripEvent.NavigateToRegisterVehicle)
            }
            CreateTripAction.OnNavigateToVehiclesList -> viewModelScope.launch {
                _events.emit(CreateTripEvent.NavigateToVehiclesList)
            }
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
        val driverId = authRepository.getCurrentUserId() ?: run {
            _state.update { it.copy(error = TripError.UserNotAuthenticated) }
            return
        }

        val departureMs = LocalDateTime(s.departureDate, s.departureTime)
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
                seatCount = s.seatCount,
                contributionPerPassenger = s.contributionPerPassenger,
                messageToPassengers = s.messageToPassengers,
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
