package com.juanpablo0612.carpool.presentation.routes.passenger_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.booking.use_case.CheckExistingBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.CreateBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.GetTripByIdUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetUserVehiclesUseCase
import com.juanpablo0612.carpool.presentation.bookings.toBookingError
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

class RouteDetailPassengerViewModel(
    private val tripId: String,
    private val getTripByIdUseCase: GetTripByIdUseCase,
    private val getUserVehiclesUseCase: GetUserVehiclesUseCase,
    private val getTripAvailableSeatsUseCase: GetTripAvailableSeatsUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
    private val checkExistingBookingUseCase: CheckExistingBookingUseCase
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
                    val alreadyRequested = checkExistingBookingUseCase(tripId)
                    _state.update { it.copy(alreadyRequested = alreadyRequested) }
                    observeVehicleAndSeats(trip.driverId, trip.vehicleId, trip.id)
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun observeVehicleAndSeats(driverId: String, vehicleId: String, tripId: String) {
        getUserVehiclesUseCase(driverId)
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
            RouteDetailPassengerAction.OnBookClick,
            RouteDetailPassengerAction.OnOpenConfirmSheet ->
                _state.update { it.copy(showConfirmSheet = true) }

            RouteDetailPassengerAction.OnDismissConfirmSheet ->
                _state.update { it.copy(showConfirmSheet = false) }

            is RouteDetailPassengerAction.OnPassengerMessageChanged ->
                _state.update { it.copy(passengerMessage = action.message.take(140)) }

            RouteDetailPassengerAction.OnConfirmBookingRequest -> book()

            RouteDetailPassengerAction.OnDismissError ->
                _state.update { it.copy(error = null) }
        }
    }

    private fun book() {
        val trip = _state.value.trip ?: return
        val vehicle = _state.value.vehicle ?: return
        _state.update { it.copy(isBooking = true, error = null, showConfirmSheet = false) }
        viewModelScope.launch {
            createBookingUseCase(
                tripId = tripId,
                driverId = trip.driverId,
                originName = trip.origin.name,
                destinationName = trip.destination.name,
                departureTime = trip.departureTime,
                totalSeats = vehicle.seatsAvailable,
                passengerMessage = _state.value.passengerMessage.ifBlank { null }
            )
                .onSuccess {
                    _state.update { it.copy(isBooking = false, alreadyRequested = true) }
                    _events.emit(RouteDetailPassengerEvent.NavigateToPassengerBookings)
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isBooking = false, error = throwable.toBookingError()) }
                }
        }
    }
}
