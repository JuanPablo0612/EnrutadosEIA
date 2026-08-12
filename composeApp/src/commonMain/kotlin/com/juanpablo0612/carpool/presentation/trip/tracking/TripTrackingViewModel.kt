package com.juanpablo0612.carpool.presentation.trip.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.use_case.GetBookingsForTripUseCase
import com.juanpablo0612.carpool.domain.places.service.LocationService
import com.juanpablo0612.carpool.domain.trip.model.PickupStatus
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.use_case.GetTripByIdFlowUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.UpdateDriverLocationUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.UpdatePassengerStatusUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.UpdateTripStatusUseCase
import com.juanpablo0612.carpool.presentation.places.add.components.LocationPermissionRequester
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TripTrackingViewModel(
    private val tripId: String,
    private val getTripByIdFlowUseCase: GetTripByIdFlowUseCase,
    private val getBookingsForTripUseCase: GetBookingsForTripUseCase,
    private val updatePassengerStatusUseCase: UpdatePassengerStatusUseCase,
    private val updateTripStatusUseCase: UpdateTripStatusUseCase,
    private val authRepository: AuthRepository,
    private val updateDriverLocationUseCase: UpdateDriverLocationUseCase,
    private val locationService: LocationService,
    private val locationPermissionRequester: LocationPermissionRequester,
) : ViewModel() {

    private val _state = MutableStateFlow(TripTrackingUiState())
    val state: StateFlow<TripTrackingUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<TripTrackingEvent>()
    val events: SharedFlow<TripTrackingEvent> = _events.asSharedFlow()

    private val currentUserId = authRepository.getCurrentUserId() ?: ""

    private var locationPollingJob: Job? = null

    init {
        observeTrip()
    }

    private fun observeTrip() {
        viewModelScope.launch {
            // The bookings query depends on whether the current user is this trip's driver, which
            // is only known once the trip itself has loaded — flatMapLatest re-subscribes the
            // party-scoped bookings flow whenever the trip emission changes, instead of nesting a
            // collect inside onEach (which would block later trip emissions from ever being
            // processed).
            getTripByIdFlowUseCase(tripId)
                .flatMapLatest { trip ->
                    val isDriver = trip?.driverId == currentUserId
                    getBookingsForTripUseCase(tripId, currentUserId, isDriver)
                        .map { bookings -> trip to bookings }
                }
                .onEach { (trip, bookings) -> applySnapshot(trip, bookings) }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect {}
        }
    }

    private fun applySnapshot(trip: Trip?, bookings: List<Booking>) {
        val isDriver = trip?.driverId == currentUserId
        val confirmedBookings = bookings.filter { it.status is BookingStatus.Confirmed }
        val passengers = confirmedBookings.map { booking ->
            PassengerWithStatus(
                passengerId = booking.passengerId,
                passengerName = booking.passengerName,
                bookingId = booking.id,
                status = PickupStatus.fromKey(
                    trip?.passengerStatuses?.get(booking.passengerId) ?: PickupStatus.Waiting.key
                )
            )
        }
        val currentPassengerBookingId = if (!isDriver)
            confirmedBookings.find { it.passengerId == currentUserId }?.id ?: ""
        else ""

        // Preserve transient UI flags (dialogs, in-flight completion) across snapshots instead of
        // rebuilding the whole state, which would silently dismiss whatever dialog was open (3.4).
        _state.update {
            it.copy(
                trip = trip,
                passengers = passengers,
                isDriver = isDriver,
                currentPassengerBookingId = currentPassengerBookingId,
                isLoading = false,
            )
        }

        updateLocationPolling(isDriver = isDriver, status = trip?.status)
    }

    fun onAction(action: TripTrackingAction) {
        when (action) {
            is TripTrackingAction.OnMarkPickedUp ->
                updatePassengerStatus(action.passengerId, PickupStatus.PickedUp)
            is TripTrackingAction.OnMarkDroppedOff ->
                updatePassengerStatus(action.passengerId, PickupStatus.DroppedOff)
            TripTrackingAction.OnCompleteTripClick ->
                _state.update { it.copy(showCompleteTripDialog = true) }
            TripTrackingAction.OnCompleteTripConfirm -> completeTrip()
            TripTrackingAction.OnCompleteTripDismiss ->
                _state.update { it.copy(showCompleteTripDialog = false) }
            TripTrackingAction.OnSOSClick ->
                _state.update { it.copy(showSosDialog = true) }
            TripTrackingAction.OnSOSDismiss ->
                _state.update { it.copy(showSosDialog = false) }
            TripTrackingAction.OnBackClick ->
                viewModelScope.launch { _events.emit(TripTrackingEvent.NavigateBack) }
            is TripTrackingAction.OnChatClick ->
                viewModelScope.launch { _events.emit(TripTrackingEvent.NavigateToChat(action.bookingId)) }
        }
    }

    private fun updatePassengerStatus(passengerId: String, status: PickupStatus) {
        viewModelScope.launch {
            updatePassengerStatusUseCase(tripId, passengerId, status)
        }
    }

    private fun completeTrip() {
        viewModelScope.launch {
            _state.update { it.copy(isCompletingTrip = true, showCompleteTripDialog = false) }
            updateTripStatusUseCase(tripId, TripStatus.Completed)
                .onSuccess { _events.emit(TripTrackingEvent.TripCompleted) }
                .onFailure { _state.update { it.copy(isCompletingTrip = false) } }
        }
    }

    // Polls the device's location and pushes it to the trip document while this user is driving
    // an in-progress trip; stops as soon as either condition stops holding. Requests the OS
    // permission once up front (3.12) so the driver is prompted as soon as the trip starts rather
    // than silently polling nothing; getCurrentCoordinates() returning null afterward (permission
    // still denied, GPS off, no fix yet) is treated as "nothing to report this tick" rather than an
    // error, so the screen just keeps showing the last known fix.
    private fun updateLocationPolling(isDriver: Boolean, status: TripStatus?) {
        val shouldPoll = isDriver && status is TripStatus.InProgress
        if (shouldPoll) {
            if (locationPollingJob == null) {
                locationPollingJob = viewModelScope.launch {
                    locationPermissionRequester.requestPermission()
                    while (true) {
                        val coordinates = locationService.getCurrentCoordinates()
                        if (coordinates != null) {
                            updateDriverLocationUseCase(tripId, coordinates.latitude, coordinates.longitude)
                        }
                        delay(LOCATION_POLL_INTERVAL_MS)
                    }
                }
            }
        } else {
            locationPollingJob?.cancel()
            locationPollingJob = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationPollingJob?.cancel()
    }

    private companion object {
        const val LOCATION_POLL_INTERVAL_MS = 10_000L
    }
}
