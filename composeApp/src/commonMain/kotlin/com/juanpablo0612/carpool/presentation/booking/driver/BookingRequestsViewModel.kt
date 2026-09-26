package com.juanpablo0612.carpool.presentation.booking.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.presentation.booking.model.toBookingWithPassenger
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.booking.usecase.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.RejectBookingUseCase
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.presentation.booking.toBookingError
import kotlinx.coroutines.Job
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
    private val bookingRepository: BookingRepository,
    private val confirmBookingUseCase: ConfirmBookingUseCase,
    private val rejectBookingUseCase: RejectBookingUseCase,
    private val cancelBookingUseCase: CancelBookingUseCase,
    private val tripRepository: TripRepository,
    private val getTripAvailableSeatsUseCase: GetTripAvailableSeatsUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BookingRequestsUiState())
    val state: StateFlow<BookingRequestsUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<BookingRequestsEvent>()
    val events: SharedFlow<BookingRequestsEvent> = _events.asSharedFlow()

    private var bookingsJob: Job? = null

    init {
        loadBookings()
    }

    private fun loadBookings() {
        // Cancel any previous collector first — Refresh calls this again, and each collector is a
        // live Firestore listener that would otherwise leak.
        bookingsJob?.cancel()
        val driverId = authRepository.getCurrentUserId() ?: run {
            _state.update { it.copy(isLoading = false, isRefreshing = false) }
            return
        }
        bookingsJob = viewModelScope.launch {
            bookingRepository.getAllDriverBookings(driverId)
                .catch { _state.update { it.copy(isLoading = false, isRefreshing = false) } }
                .collect { bookings ->
                    val items = bookings.map { it.toBookingWithPassenger() }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
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
            is BookingRequestsAction.OnRateBooking -> viewModelScope.launch {
                _events.emit(
                    BookingRequestsEvent.NavigateToRating(
                        bookingId = action.bookingId,
                        tripId = action.tripId,
                        rateeId = action.rateeId,
                        rateeName = action.rateeName
                    )
                )
            }
            is BookingRequestsAction.Refresh -> {
                _state.update { it.copy(isRefreshing = true) }
                loadBookings()
            }
            is BookingRequestsAction.OnHistoryQueryChange -> _state.update { it.copy(historyQuery = action.query) }
            BookingRequestsAction.DismissError -> _state.update { it.copy(error = null) }
            BookingRequestsAction.DismissTripFilledNotice -> _state.update { it.copy(tripJustFilled = false) }
        }
    }

    private fun acceptBooking(bookingId: String, tripId: String) {
        if (bookingId in _state.value.processingIds) return
        viewModelScope.launch {
            _state.update { it.copy(processingIds = it.processingIds + bookingId) }
            confirmBookingUseCase(bookingId, tripId)
                .onSuccess {
                    checkTripFull(tripId)
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.toBookingError()) }
                }
            _state.update { it.copy(processingIds = it.processingIds - bookingId) }
        }
    }

    private fun rejectBooking(bookingId: String, reason: com.juanpablo0612.carpool.domain.booking.model.RejectReason, comment: String?) {
        if (bookingId in _state.value.processingIds) return
        viewModelScope.launch {
            _state.update { it.copy(processingIds = it.processingIds + bookingId) }
            rejectBookingUseCase(bookingId, reason, comment)
                .onFailure { error ->
                    _state.update { it.copy(error = error.toBookingError()) }
                }
            _state.update { it.copy(processingIds = it.processingIds - bookingId) }
        }
    }

    private fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            _state.update { it.copy(processingIds = it.processingIds + bookingId) }
            cancelBookingUseCase(bookingId)
                .onFailure { error ->
                    _state.update { it.copy(error = error.toBookingError()) }
                }
            _state.update { it.copy(processingIds = it.processingIds - bookingId) }
        }
    }

    private fun checkTripFull(tripId: String) {
        viewModelScope.launch {
            tripRepository.getTripById(tripId).getOrNull() ?: return@launch
            val available = getTripAvailableSeatsUseCase(tripId).first()
            if (available == 0) {
                _state.update { it.copy(tripJustFilled = true) }
            }
        }
    }
}
