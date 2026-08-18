package com.juanpablo0612.carpool.presentation.trip.passengerdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.usecase.GetUserPublicProfileUseCase
import com.juanpablo0612.carpool.domain.booking.model.BookingError
import com.juanpablo0612.carpool.domain.booking.usecase.CheckExistingBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.CreateBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.GetTripByIdUseCase
import com.juanpablo0612.carpool.domain.vehicle.usecase.GetUserVehiclesUseCase
import com.juanpablo0612.carpool.presentation.booking.toBookingError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class RouteDetailPassengerViewModel(
    private val tripId: String,
    private val getTripByIdUseCase: GetTripByIdUseCase,
    private val getUserVehiclesUseCase: GetUserVehiclesUseCase,
    private val getTripAvailableSeatsUseCase: GetTripAvailableSeatsUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
    private val checkExistingBookingUseCase: CheckExistingBookingUseCase,
    private val getUserPublicProfileUseCase: GetUserPublicProfileUseCase
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
                    loadDriverProfile(trip.driverId)
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }

    // Available seats are driven by trip.seatCount, not the vehicle's capacity (3.1) — the vehicle
    // flow is only consulted here for display (photo/brand/model). flatMapLatest re-subscribes the
    // seats flow whenever the vehicle list changes, instead of collecting it nested inside onEach,
    // which would block this flow from ever processing a later vehicle emission (3.6).
    private fun observeVehicleAndSeats(driverId: String, vehicleId: String, tripId: String) {
        getUserVehiclesUseCase(driverId)
            .flatMapLatest { vehicles ->
                val vehicle = vehicles.find { it.id == vehicleId }
                getTripAvailableSeatsUseCase(tripId).map { seats -> vehicle to seats }
            }
            .onEach { (vehicle, seats) ->
                _state.update { it.copy(vehicle = vehicle, availableSeats = seats) }
            }
            .launchIn(viewModelScope)
    }

    // A failed profile fetch degrades to null (Driver section falls back to the placeholder
    // label) — it must never fail the whole trip-detail load.
    private fun loadDriverProfile(driverId: String) {
        viewModelScope.launch {
            val profile = getUserPublicProfileUseCase(driverId).getOrNull()
            _state.update { it.copy(driver = profile) }
        }
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
        if (_state.value.vehicle == null) {
            // The vehicle is only used for display here (seats come from trip.seatCount, 3.1),
            // but a missing vehicle still means the trip's data is incomplete — surface it instead
            // of leaving the confirm button silently doing nothing (3.7).
            _state.update { it.copy(error = BookingError.VehicleNotFound) }
            return
        }
        _state.update { it.copy(isBooking = true, error = null, showConfirmSheet = false) }
        viewModelScope.launch {
            createBookingUseCase(
                tripId = tripId,
                driverId = trip.driverId,
                originName = trip.origin.name,
                destinationName = trip.destination.name,
                departureTime = trip.departureTime,
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
