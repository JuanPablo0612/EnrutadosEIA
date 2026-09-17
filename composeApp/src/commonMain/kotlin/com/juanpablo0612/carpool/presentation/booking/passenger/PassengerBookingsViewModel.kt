package com.juanpablo0612.carpool.presentation.booking.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.booking.usecase.CancelBookingUseCase
import com.juanpablo0612.carpool.presentation.booking.toBookingError
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
    private val bookingRepository: BookingRepository,
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
            bookingRepository.getPassengerBookings(userId)
                .onEach { bookings ->
                    _state.update { it.copy(bookings = bookings, isLoading = false) }
                    resolveDriverNames(bookings)
                }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect {}
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
                _events.emit(
                    PassengerBookingsEvent.NavigateToRating(
                        bookingId = action.bookingId,
                        tripId = action.tripId,
                        rateeId = action.rateeId,
                        rateeName = action.rateeName
                    )
                )
            }
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
