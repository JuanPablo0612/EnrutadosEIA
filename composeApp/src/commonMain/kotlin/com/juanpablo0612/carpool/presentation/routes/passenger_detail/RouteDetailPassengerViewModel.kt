package com.juanpablo0612.carpool.presentation.routes.passenger_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.booking.use_case.CreateBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.GetTripByIdUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetDriverVehiclesUseCase
import com.juanpablo0612.carpool.presentation.bookings.toBookingError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RouteDetailPassengerViewModel(
    private val tripId: String,
    private val getTripByIdUseCase: GetTripByIdUseCase,
    private val getDriverVehiclesUseCase: GetDriverVehiclesUseCase,
    private val getTripAvailableSeatsUseCase: GetTripAvailableSeatsUseCase,
    private val createBookingUseCase: CreateBookingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RouteDetailPassengerUiState())
    val state: StateFlow<RouteDetailPassengerUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<RouteDetailPassengerEvent>()
    val events: SharedFlow<RouteDetailPassengerEvent> = _events.asSharedFlow()

    init {
        loadTrip()
    }

    private fun loadTrip() {
        viewModelScope.launch {
            getTripByIdUseCase(tripId)
                .onSuccess { trip ->
                    _state.update { it.copy(isLoading = false, trip = trip) }
                    observeVehicleAndSeats(trip.driverId, trip.vehicleId, trip.id)
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun observeVehicleAndSeats(driverId: String, vehicleId: String, tripId: String) {
        getDriverVehiclesUseCase(driverId)
            .onEach { vehicles ->
                val vehicle = vehicles.find { it.id == vehicleId }
                _state.update { it.copy(vehicle = vehicle) }
                if (vehicle != null) {
                    getTripAvailableSeatsUseCase(tripId, vehicle.seatsAvailable)
                        .collect { seats -> _state.update { it.copy(availableSeats = seats) } }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: RouteDetailPassengerAction) {
        when (action) {
            RouteDetailPassengerAction.OnBackClick -> viewModelScope.launch {
                _events.emit(RouteDetailPassengerEvent.NavigateBack)
            }
            RouteDetailPassengerAction.OnBookClick -> book()
            RouteDetailPassengerAction.OnDismissError -> _state.update { it.copy(error = null) }
        }
    }

    private fun book() {
        val trip = _state.value.trip ?: return
        val vehicle = _state.value.vehicle ?: return
        _state.update { it.copy(isBooking = true, error = null) }
        viewModelScope.launch {
            createBookingUseCase(tripId, trip.driverId, vehicle.seatsAvailable)
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
