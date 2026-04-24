package com.juanpablo0612.carpool.presentation.routes.passenger_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.use_case.CreateBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetVehicleAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.GetRouteByIdUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetDriverVehiclesUseCase
import com.juanpablo0612.carpool.presentation.bookings.toBookingError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RouteDetailPassengerViewModel(
    private val routeId: String,
    private val getRouteByIdUseCase: GetRouteByIdUseCase,
    private val getDriverVehiclesUseCase: GetDriverVehiclesUseCase,
    private val getVehicleAvailableSeatsUseCase: GetVehicleAvailableSeatsUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RouteDetailPassengerUiState())
    val state: StateFlow<RouteDetailPassengerUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<RouteDetailPassengerEvent>()
    val events: SharedFlow<RouteDetailPassengerEvent> = _events.asSharedFlow()

    init {
        loadRoute()
    }

    private fun loadRoute() {
        viewModelScope.launch {
            getRouteByIdUseCase(routeId)
                .onSuccess { route ->
                    _state.update { it.copy(isLoading = false, route = route) }
                    observeVehiclesWithSeats(route.driverId)
                }
                .onFailure {
                    _state.update { s -> s.copy(isLoading = false) }
                }
        }
    }

    private fun observeVehiclesWithSeats(driverId: String) {
        getDriverVehiclesUseCase(driverId)
            .onEach { vehicles ->
                if (vehicles.isEmpty()) {
                    _state.update { it.copy(vehiclesWithSeats = emptyList()) }
                    return@onEach
                }
                val seatFlows = vehicles.map { vehicle ->
                    getVehicleAvailableSeatsUseCase(routeId, vehicle.id, vehicle.seatsAvailable)
                        .map { seats -> VehicleWithAvailableSeats(vehicle, seats) }
                }
                combine(seatFlows) { it.toList() }
                    .collect { vehiclesWithSeats ->
                        _state.update { it.copy(vehiclesWithSeats = vehiclesWithSeats) }
                    }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: RouteDetailPassengerAction) {
        when (action) {
            RouteDetailPassengerAction.OnBackClick -> viewModelScope.launch {
                _events.emit(RouteDetailPassengerEvent.NavigateBack)
            }
            is RouteDetailPassengerAction.OnBookClick -> book(action.vehicleId, action.totalSeats)
            RouteDetailPassengerAction.OnDismissError -> _state.update { it.copy(error = null) }
        }
    }

    private fun book(vehicleId: String, totalSeats: Int) {
        val route = _state.value.route ?: return
        _state.update { it.copy(isBooking = true, error = null) }
        viewModelScope.launch {
            createBookingUseCase(routeId, vehicleId, route.driverId, totalSeats)
                .onSuccess {
                    _state.update { it.copy(isBooking = false) }
                    _events.emit(RouteDetailPassengerEvent.BookingCreated)
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isBooking = false, error = throwable.toBookingError()) }
                }
        }
    }
}
