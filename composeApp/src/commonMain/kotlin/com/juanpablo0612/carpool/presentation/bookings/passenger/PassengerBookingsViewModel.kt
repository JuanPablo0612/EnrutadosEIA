package com.juanpablo0612.carpool.presentation.bookings.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.use_case.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetPassengerBookingsUseCase
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

class PassengerBookingsViewModel(
    private val getPassengerBookingsUseCase: GetPassengerBookingsUseCase,
    private val cancelBookingUseCase: CancelBookingUseCase,
    private val authRepository: AuthRepository
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
            getPassengerBookingsUseCase(userId)
                .onEach { bookings -> _state.update { it.copy(bookings = bookings, isLoading = false) } }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect {}
        }
    }

    fun onAction(action: PassengerBookingsAction) {
        when (action) {
            PassengerBookingsAction.OnBackClick -> viewModelScope.launch {
                _events.emit(PassengerBookingsEvent.NavigateBack)
            }
            is PassengerBookingsAction.OnCancelBookingClick -> cancelBooking(action.bookingId)
        }
    }

    private fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            cancelBookingUseCase(bookingId).onFailure { throwable ->
                _state.update { it.copy(error = throwable.toBookingError()) }
            }
        }
    }
}
