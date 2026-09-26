package com.juanpablo0612.carpool.presentation.booking.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import com.juanpablo0612.carpool.domain.booking.usecase.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.GetBookingsForTripUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.RejectBookingUseCase
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.presentation.booking.model.toBookingWithPassenger
import com.juanpablo0612.carpool.presentation.booking.toBookingError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripPassengersViewModel(
    private val tripId: String,
    private val getBookingsForTripUseCase: GetBookingsForTripUseCase,
    private val confirmBookingUseCase: ConfirmBookingUseCase,
    private val rejectBookingUseCase: RejectBookingUseCase,
    private val cancelBookingUseCase: CancelBookingUseCase,
    private val authRepository: AuthRepository,
    private val tripRepository: TripRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TripPassengersUiState())
    val state: StateFlow<TripPassengersUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<TripPassengersEvent>()
    val events: SharedFlow<TripPassengersEvent> = _events.asSharedFlow()

    init {
        loadTrip()
        loadBookings()
    }

    // Fetched directly from the trip itself rather than derived from the first pending/confirmed
    // booking, so the trip-context header still renders even when a trip has zero bookings.
    private fun loadTrip() {
        viewModelScope.launch {
            tripRepository.getTripById(tripId)
                .onSuccess { trip -> _state.update { it.copy(trip = trip) } }
        }
    }

    private fun loadBookings() {
        val driverId = authRepository.getCurrentUserId() ?: run {
            _state.update { it.copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            getBookingsForTripUseCase(tripId, driverId, isDriver = true)
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect { bookings ->
                    val items = bookings.map { it.toBookingWithPassenger() }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            pending = items
                                .filter { b -> b.booking.status is BookingStatus.Pending }
                                .sortedByDescending { b -> b.booking.createdAt },
                            confirmed = items
                                .filter { b -> b.booking.status is BookingStatus.Confirmed }
                                .sortedBy { b -> b.booking.departureTime },
                        )
                    }
                }
        }
    }

    // Bookings are already live via the persistent collector started in init — re-subscribing on
    // every pull-to-refresh would stack up duplicate collectors. Only the trip needs a fresh
    // one-shot fetch here; the refreshing indicator clears once that resolves.
    private fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            tripRepository.getTripById(tripId)
                .onSuccess { trip -> _state.update { it.copy(trip = trip) } }
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun onAction(action: TripPassengersAction) {
        when (action) {
            is TripPassengersAction.Accept -> acceptBooking(action.bookingId, action.tripId)
            is TripPassengersAction.OpenReject -> _state.update {
                it.copy(
                    pendingRejectionFor = action.bookingId,
                    selectedRejectReason = null,
                    rejectComment = "",
                )
            }
            is TripPassengersAction.SelectRejectReason -> _state.update {
                it.copy(selectedRejectReason = action.reason)
            }
            is TripPassengersAction.UpdateRejectComment -> _state.update {
                it.copy(rejectComment = action.comment)
            }
            is TripPassengersAction.ConfirmReject -> {
                val reason = _state.value.selectedRejectReason ?: return
                val comment = _state.value.rejectComment.takeIf { it.isNotBlank() }
                _state.update { it.copy(pendingRejectionFor = null) }
                rejectBooking(action.bookingId, reason, comment)
            }
            is TripPassengersAction.DismissReject -> _state.update { it.copy(pendingRejectionFor = null) }
            is TripPassengersAction.OpenCancelConfirmed -> _state.update {
                it.copy(cancelConfirmFor = action.bookingId)
            }
            is TripPassengersAction.DismissCancelConfirmed -> _state.update { it.copy(cancelConfirmFor = null) }
            is TripPassengersAction.CancelConfirmed -> {
                _state.update { it.copy(cancelConfirmFor = null) }
                cancelBooking(action.bookingId)
            }
            is TripPassengersAction.OpenPassengerProfile -> viewModelScope.launch {
                _events.emit(TripPassengersEvent.NavigateToPassengerProfile(action.passengerId))
            }
            is TripPassengersAction.OnRateBooking -> viewModelScope.launch {
                _events.emit(
                    TripPassengersEvent.NavigateToRating(
                        bookingId = action.bookingId,
                        tripId = action.tripId,
                        rateeId = action.rateeId,
                        rateeName = action.rateeName,
                    )
                )
            }
            TripPassengersAction.DismissError -> _state.update { it.copy(error = null) }
            TripPassengersAction.Refresh -> refresh()
        }
    }

    private fun acceptBooking(bookingId: String, tripId: String) {
        if (bookingId in _state.value.processingIds) return
        viewModelScope.launch {
            _state.update { it.copy(processingIds = it.processingIds + bookingId) }
            confirmBookingUseCase(bookingId, tripId)
                .onFailure { error -> _state.update { it.copy(error = error.toBookingError()) } }
            _state.update { it.copy(processingIds = it.processingIds - bookingId) }
        }
    }

    private fun rejectBooking(bookingId: String, reason: RejectReason, comment: String?) {
        if (bookingId in _state.value.processingIds) return
        viewModelScope.launch {
            _state.update { it.copy(processingIds = it.processingIds + bookingId) }
            rejectBookingUseCase(bookingId, reason, comment)
                .onFailure { error -> _state.update { it.copy(error = error.toBookingError()) } }
            _state.update { it.copy(processingIds = it.processingIds - bookingId) }
        }
    }

    private fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            _state.update { it.copy(processingIds = it.processingIds + bookingId) }
            cancelBookingUseCase(bookingId)
                .onFailure { error -> _state.update { it.copy(error = error.toBookingError()) } }
            _state.update { it.copy(processingIds = it.processingIds - bookingId) }
        }
    }
}
