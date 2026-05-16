package com.juanpablo0612.carpool.presentation.trip.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.use_case.GetBookingsForTripUseCase
import com.juanpablo0612.carpool.domain.trip.model.PickupStatus
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.use_case.GetTripByIdFlowUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.UpdatePassengerStatusUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.UpdateTripStatusUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripTrackingViewModel(
    private val tripId: String,
    private val getTripByIdFlowUseCase: GetTripByIdFlowUseCase,
    private val getBookingsForTripUseCase: GetBookingsForTripUseCase,
    private val updatePassengerStatusUseCase: UpdatePassengerStatusUseCase,
    private val updateTripStatusUseCase: UpdateTripStatusUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TripTrackingUiState())
    val state: StateFlow<TripTrackingUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<TripTrackingEvent>()
    val events: SharedFlow<TripTrackingEvent> = _events.asSharedFlow()

    private val currentUserId = authRepository.getCurrentUserId() ?: ""

    init {
        observeTrip()
    }

    private fun observeTrip() {
        viewModelScope.launch {
            combine(
                getTripByIdFlowUseCase(tripId),
                getBookingsForTripUseCase(tripId)
            ) { trip, bookings ->
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

                TripTrackingUiState(
                    trip = trip,
                    passengers = passengers,
                    isDriver = isDriver,
                    currentPassengerBookingId = currentPassengerBookingId,
                    isLoading = false
                )
            }
                .onEach { newState -> _state.update { newState } }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect {}
        }
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
}
