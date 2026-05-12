package com.juanpablo0612.carpool.presentation.bookings.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.model.BookingWithPassenger
import com.juanpablo0612.carpool.domain.booking.model.PassengerSummary
import com.juanpablo0612.carpool.domain.booking.model.TripSummary
import com.juanpablo0612.carpool.domain.booking.use_case.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetAllDriverBookingsUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.RejectBookingUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.GetTripByIdUseCase
import com.juanpablo0612.carpool.presentation.bookings.toBookingError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookingRequestsViewModel(
    private val getAllDriverBookingsUseCase: GetAllDriverBookingsUseCase,
    private val confirmBookingUseCase: ConfirmBookingUseCase,
    private val rejectBookingUseCase: RejectBookingUseCase,
    private val cancelBookingUseCase: CancelBookingUseCase,
    private val getTripByIdUseCase: GetTripByIdUseCase,
    private val getTripAvailableSeatsUseCase: GetTripAvailableSeatsUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BookingRequestsUiState())
    val state: StateFlow<BookingRequestsUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<BookingRequestsEvent>()
    val events: SharedFlow<BookingRequestsEvent> = _events.asSharedFlow()

    init {
        loadBookings()
    }

    private fun loadBookings() {
        val driverId = authRepository.getCurrentUserId() ?: run {
            _state.update { it.copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            getAllDriverBookingsUseCase(driverId)
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
                            history = items
                                .filter { b ->
                                    b.booking.status is BookingStatus.Rejected ||
                                        b.booking.status is BookingStatus.Cancelled
                                }
                                .sortedByDescending { b -> b.booking.departureTime },
                        )
                    }
                }
        }
    }

    fun onAction(action: BookingRequestsAction) {
        when (action) {
            is BookingRequestsAction.SelectTab -> _state.update { it.copy(tab = action.tab) }
            is BookingRequestsAction.Accept -> acceptBooking(action.bookingId, action.tripId)
            is BookingRequestsAction.OpenReject -> _state.update {
                it.copy(
                    pendingRejectionFor = action.bookingId,
                    selectedRejectReason = null,
                    rejectComment = "",
                )
            }
            is BookingRequestsAction.SelectRejectReason -> _state.update {
                it.copy(selectedRejectReason = action.reason)
            }
            is BookingRequestsAction.UpdateRejectComment -> _state.update {
                it.copy(rejectComment = action.comment)
            }
            is BookingRequestsAction.ConfirmReject -> {
                val reason = _state.value.selectedRejectReason ?: return
                val comment = _state.value.rejectComment.takeIf { it.isNotBlank() }
                _state.update { it.copy(pendingRejectionFor = null) }
                rejectBooking(action.bookingId, reason, comment)
            }
            is BookingRequestsAction.DismissReject -> _state.update {
                it.copy(pendingRejectionFor = null)
            }
            is BookingRequestsAction.OpenCancelConfirmed -> _state.update {
                it.copy(cancelConfirmFor = action.bookingId)
            }
            is BookingRequestsAction.DismissCancelConfirmed -> _state.update {
                it.copy(cancelConfirmFor = null)
            }
            is BookingRequestsAction.CancelConfirmed -> {
                _state.update { it.copy(cancelConfirmFor = null) }
                cancelBooking(action.bookingId)
            }
            is BookingRequestsAction.OpenPassengerProfile -> viewModelScope.launch {
                _events.emit(BookingRequestsEvent.NavigateToPassengerProfile(action.passengerId))
            }
            is BookingRequestsAction.Refresh -> loadBookings()
            is BookingRequestsAction.DismissSnackbar -> _state.update {
                it.copy(snackbarMessage = null)
            }
        }
    }

    private fun acceptBooking(bookingId: String, tripId: String) {
        viewModelScope.launch {
            _state.update { it.copy(processingIds = it.processingIds + bookingId) }
            confirmBookingUseCase(bookingId)
                .onSuccess {
                    checkTripFull(tripId)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(snackbarMessage = error.message)
                    }
                    error.toBookingError()
                }
            _state.update { it.copy(processingIds = it.processingIds - bookingId) }
        }
    }

    private fun rejectBooking(bookingId: String, reason: com.juanpablo0612.carpool.domain.booking.model.RejectReason, comment: String?) {
        viewModelScope.launch {
            _state.update { it.copy(processingIds = it.processingIds + bookingId) }
            rejectBookingUseCase(bookingId, reason, comment)
                .onFailure { error ->
                    _state.update { it.copy(snackbarMessage = error.message) }
                }
            _state.update { it.copy(processingIds = it.processingIds - bookingId) }
        }
    }

    private fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            _state.update { it.copy(processingIds = it.processingIds + bookingId) }
            cancelBookingUseCase(bookingId)
                .onFailure { error ->
                    _state.update { it.copy(snackbarMessage = error.message) }
                }
            _state.update { it.copy(processingIds = it.processingIds - bookingId) }
        }
    }

    private fun checkTripFull(tripId: String) {
        viewModelScope.launch {
            val trip = getTripByIdUseCase(tripId).getOrNull() ?: return@launch
            val available = getTripAvailableSeatsUseCase(tripId, trip.seatCount).first()
            if (available == 0) {
                _state.update { it.copy(snackbarMessage = TRIP_FULL_SIGNAL) }
            }
        }
    }

    companion object {
        const val TRIP_FULL_SIGNAL = "TRIP_FULL"
    }
}

private fun Booking.toBookingWithPassenger() = BookingWithPassenger(
    booking = this,
    passenger = PassengerSummary(
        id = passengerId,
        name = passengerName,
        averageRating = null,
        tripsCompleted = 0,
        isEiaVerified = passengerEmail.endsWith("@eia.edu.co"),
    ),
    tripSummary = TripSummary(
        tripId = tripId,
        originName = originName,
        destinationName = destinationName,
        departureAt = departureTime,
    ),
)
