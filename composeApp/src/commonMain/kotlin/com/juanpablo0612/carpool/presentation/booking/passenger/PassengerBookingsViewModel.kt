package com.juanpablo0612.carpool.presentation.booking.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.booking.usecase.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import com.juanpablo0612.carpool.presentation.booking.toBookingError
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

class PassengerBookingsViewModel(
    private val bookingRepository: BookingRepository,
    private val cancelBookingUseCase: CancelBookingUseCase,
    private val authRepository: AuthRepository,
    private val tripRepository: TripRepository,
    private val vehicleRepository: VehicleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PassengerBookingsUiState())
    val state: StateFlow<PassengerBookingsUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<PassengerBookingsEvent>()
    val events: SharedFlow<PassengerBookingsEvent> = _events.asSharedFlow()

    init {
        loadBookings()
    }

    private fun loadBookings() {
        val userId = authRepository.getCurrentUserId() ?: run {
            _state.update { it.copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            bookingRepository.getPassengerBookings(userId)
                .onEach { bookings ->
                    _state.update { it.copy(bookings = bookings, isLoading = false) }
                    resolveDriverNames(bookings)
                    resolveVehicleSummaries(bookings)
                }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect {}
        }
    }

    // Booking carries no vehicleId of its own — the trip it's for does, so resolving "what car
    // is this" means one hop through the trip rather than a Booking schema change. One fetch per
    // distinct tripId not already cached, not per booking.
    private fun resolveVehicleSummaries(bookings: List<Booking>) {
        val missingTripIds = bookings.map { it.tripId }.distinct() - _state.value.vehicleSummaries.keys
        if (missingTripIds.isEmpty()) return
        viewModelScope.launch {
            val resolved = mutableMapOf<String, String>()
            for (tripId in missingTripIds) {
                val trip = tripRepository.getTripById(tripId).getOrNull() ?: continue
                val vehicle = vehicleRepository.getUserVehicles(trip.driverId).first()
                    .find { it.id == trip.vehicleId } ?: continue
                resolved[tripId] = "${vehicle.brand} ${vehicle.model} · ${vehicle.color}"
            }
            if (resolved.isNotEmpty()) {
                _state.update { it.copy(vehicleSummaries = it.vehicleSummaries + resolved) }
            }
        }
    }

    // One driver with several bookings must cost one profile read, not one per booking — fetch
    // each distinct driverId not already cached, exactly once.
    private fun resolveDriverNames(bookings: List<Booking>) {
        val missingIds = bookings.map { it.driverId }.distinct() - _state.value.driverNames.keys
        if (missingIds.isEmpty()) return
        viewModelScope.launch {
            val resolved = missingIds.associateWith { driverId ->
                authRepository.getPublicProfile(driverId).getOrNull()?.name
            }.filterValues { it != null }.mapValues { it.value!! }
            if (resolved.isNotEmpty()) {
                _state.update { it.copy(driverNames = it.driverNames + resolved) }
            }
        }
    }

    fun onAction(action: PassengerBookingsAction) {
        when (action) {
            PassengerBookingsAction.OnBackClick -> viewModelScope.launch {
                _events.emit(PassengerBookingsEvent.NavigateBack)
            }

            is PassengerBookingsAction.OnTabSelected ->
                _state.update { it.copy(selectedTab = action.tab) }

            is PassengerBookingsAction.OnCancelBookingClick ->
                _state.update { it.copy(showCancelConfirmFor = action.bookingId) }

            is PassengerBookingsAction.OnConfirmCancel -> {
                _state.update { it.copy(showCancelConfirmFor = null, cancellingBookingId = action.bookingId) }
                cancelBooking(action.bookingId)
            }

            PassengerBookingsAction.OnDismissCancelDialog ->
                _state.update { it.copy(showCancelConfirmFor = null) }

            PassengerBookingsAction.OnDismissError ->
                _state.update { it.copy(error = null) }

            is PassengerBookingsAction.OnTrackTrip -> viewModelScope.launch {
                _events.emit(PassengerBookingsEvent.NavigateToTripTracking(action.tripId))
            }

            is PassengerBookingsAction.OnRateBooking -> viewModelScope.launch {
                // The screen resolves rateeName from the ViewModel's driverNames cache, which is
                // populated by a separate async fetch that can still be in flight when the user
                // taps Rate — re-resolve authoritatively here instead of trusting a possibly-empty
                // value that raced the fetch.
                val rateeName = _state.value.driverNames[action.rateeId]
                    ?: authRepository.getPublicProfile(action.rateeId).getOrNull()?.name
                    ?: action.rateeName
                _events.emit(
                    PassengerBookingsEvent.NavigateToRating(
                        bookingId = action.bookingId,
                        tripId = action.tripId,
                        rateeId = action.rateeId,
                        rateeName = rateeName
                    )
                )
            }

            PassengerBookingsAction.Refresh -> refresh()

            is PassengerBookingsAction.OnPastSearchQueryChanged ->
                _state.update { it.copy(pastSearchQuery = action.query) }

            PassengerBookingsAction.OnSearchTripsClick -> viewModelScope.launch {
                _events.emit(PassengerBookingsEvent.NavigateToSearchTrips)
            }

            is PassengerBookingsAction.OnMessageDriver -> viewModelScope.launch {
                _events.emit(
                    PassengerBookingsEvent.NavigateToChat(
                        bookingId = action.bookingId,
                        tripId = action.tripId,
                        otherPartyName = action.driverName,
                        isReadOnly = action.isReadOnly
                    )
                )
            }
        }
    }

    // The bookings list is already live via the persistent collector started in init — refresh
    // just needs a one-shot fetch to resolve the pull-to-refresh indicator, not a second
    // subscription stacked on top of it.
    private fun refresh() {
        val userId = authRepository.getCurrentUserId() ?: return
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            runCatching { bookingRepository.getPassengerBookings(userId).first() }
                .onSuccess { bookings -> _state.update { it.copy(bookings = bookings) } }
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            cancelBookingUseCase(bookingId)
                .onSuccess { _state.update { it.copy(cancellingBookingId = null) } }
                .onFailure { throwable ->
                    _state.update { it.copy(cancellingBookingId = null, error = throwable.toBookingError()) }
                }
        }
    }
}
