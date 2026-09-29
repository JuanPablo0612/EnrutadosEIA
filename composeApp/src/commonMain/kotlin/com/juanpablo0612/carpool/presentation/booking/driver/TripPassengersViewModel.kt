package com.juanpablo0612.carpool.presentation.booking.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.booking.usecase.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.RejectBookingUseCase
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.presentation.booking.BookingError
import com.juanpablo0612.carpool.presentation.booking.driver.decision.BookingDecisions
import com.juanpablo0612.carpool.presentation.booking.toBookingError
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * One trip's passengers, for its driver: two live listeners, the trip (seats and status) and its
 * open bookings, so accepting a request or a passenger cancelling shows up without refreshing.
 */
class TripPassengersViewModel(
    private val tripId: String,
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
    private val tripRepository: TripRepository,
    confirmBooking: ConfirmBookingUseCase,
    rejectBooking: RejectBookingUseCase,
    cancelBooking: CancelBookingUseCase,
) : ViewModel() {

    private val decisions = BookingDecisions(confirmBooking, rejectBooking, cancelBooking, viewModelScope)

    private val screenState = MutableStateFlow(TripPassengersUiState())
    val state: StateFlow<TripPassengersUiState> = combine(screenState, decisions.state) { screen, decisions ->
        screen.copy(decisions = decisions)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TripPassengersUiState())

    private val _events = MutableSharedFlow<TripPassengersEvent>()
    val events: SharedFlow<TripPassengersEvent> = _events.asSharedFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun onAction(action: TripPassengersAction) {
        when (action) {
            is TripPassengersAction.OnViewProfile ->
                emit(TripPassengersEvent.NavigateToPassengerProfile(action.passengerId))
            is TripPassengersAction.OnMessagePassenger -> emit(
                TripPassengersEvent.NavigateToChat(
                    bookingId = action.booking.id,
                    tripId = tripId,
                    passengerName = action.booking.passengerName,
                    isReadOnly = screenState.value.isFinished,
                )
            )
            is TripPassengersAction.OnRatePassenger -> emit(
                TripPassengersEvent.NavigateToRating(
                    bookingId = action.booking.id,
                    tripId = tripId,
                    rateeId = action.booking.passengerId,
                    rateeName = action.booking.passengerName,
                )
            )
            is TripPassengersAction.OnDecision -> decisions.onAction(action.action)
            TripPassengersAction.OnRetry -> load()
        }
    }

    private fun load() {
        loadJob?.cancel()
        val driverId = authRepository.getCurrentUserId() ?: run {
            screenState.update { it.copy(isLoading = false, loadError = BookingError.NotAuthenticated) }
            return
        }
        screenState.update { it.copy(isLoading = true, loadError = null) }
        loadJob = viewModelScope.launch {
            combine(
                tripRepository.getTripByIdFlow(tripId),
                bookingRepository.getOpenBookingsForTrip(tripId, driverId),
            ) { trip, bookings ->
                screenState.update {
                    it.copy(
                        isLoading = false,
                        // A trip that no longer exists has nothing to show.
                        loadError = if (trip == null) BookingError.Unknown else null,
                        trip = trip,
                        pending = bookings.filter { b -> b.status == BookingStatus.Pending }.sortedBy { b -> b.createdAt },
                        confirmed = bookings.filter { b -> b.status == BookingStatus.Confirmed }.sortedBy { b -> b.createdAt },
                    )
                }
            }
                .catch { error -> screenState.update { it.copy(isLoading = false, loadError = error.toBookingError()) } }
                .collect {}
        }
    }

    private fun emit(event: TripPassengersEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}
