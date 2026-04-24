package com.juanpablo0612.carpool.presentation.bookings.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.use_case.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetDriverBookingRequestsUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.RejectBookingUseCase
import com.juanpablo0612.carpool.presentation.bookings.toBookingError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookingRequestsViewModel(
    private val getDriverBookingRequestsUseCase: GetDriverBookingRequestsUseCase,
    private val confirmBookingUseCase: ConfirmBookingUseCase,
    private val rejectBookingUseCase: RejectBookingUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BookingRequestsUiState())
    val state: StateFlow<BookingRequestsUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<BookingRequestsEvent>()
    val events: SharedFlow<BookingRequestsEvent> = _events.asSharedFlow()

    init {
        loadRequests()
    }

    private fun loadRequests() {
        val driverId = authRepository.getCurrentUserId() ?: run {
            _state.update { it.copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            getDriverBookingRequestsUseCase(driverId)
                .onEach { requests -> _state.update { it.copy(requests = requests, isLoading = false) } }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect {}
        }
    }

    fun onAction(action: BookingRequestsAction) {
        when (action) {
            is BookingRequestsAction.OnConfirmClick -> confirmBooking(action.bookingId)
            is BookingRequestsAction.OnRejectClick -> rejectBooking(action.bookingId)
        }
    }

    private fun confirmBooking(bookingId: String) {
        viewModelScope.launch {
            _state.update { it.copy(processingIds = it.processingIds + bookingId) }
            confirmBookingUseCase(bookingId)
                .onFailure { error ->
                    _state.update { it.copy(error = error.toBookingError()) }
                }
            _state.update { it.copy(processingIds = it.processingIds - bookingId) }
        }
    }

    private fun rejectBooking(bookingId: String) {
        viewModelScope.launch {
            _state.update { it.copy(processingIds = it.processingIds + bookingId) }
            rejectBookingUseCase(bookingId)
                .onFailure { error ->
                    _state.update { it.copy(error = error.toBookingError()) }
                }
            _state.update { it.copy(processingIds = it.processingIds - bookingId) }
        }
    }
}
