package com.juanpablo0612.carpool.presentation.booking.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.booking.usecase.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.RejectBookingUseCase
import com.juanpablo0612.carpool.presentation.booking.BookingError
import com.juanpablo0612.carpool.presentation.booking.driver.decision.BookingDecisions
import com.juanpablo0612.carpool.presentation.booking.toBookingError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

/**
 * "Solicitudes": the driver's open bookings on upcoming trips, grouped by trip. One listener for
 * the bookings (the query leaves closed requests and past trips on the server) and one per trip
 * that has any, for its free seats; requester details come from the bookings themselves.
 */
class BookingRequestsViewModel(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
    private val getTripAvailableSeats: GetTripAvailableSeatsUseCase,
    confirmBooking: ConfirmBookingUseCase,
    rejectBooking: RejectBookingUseCase,
    cancelBooking: CancelBookingUseCase,
) : ViewModel() {

    private val decisions = BookingDecisions(confirmBooking, rejectBooking, cancelBooking, viewModelScope)

    private val screenState = MutableStateFlow(BookingRequestsUiState())
    val state: StateFlow<BookingRequestsUiState> = combine(screenState, decisions.state) { screen, decisions ->
        screen.copy(decisions = decisions)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BookingRequestsUiState())

    private val _events = MutableSharedFlow<BookingRequestsEvent>()
    val events: SharedFlow<BookingRequestsEvent> = _events.asSharedFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun onAction(action: BookingRequestsAction) {
        when (action) {
            is BookingRequestsAction.OnTabSelected -> screenState.update { it.copy(tab = action.tab) }
            is BookingRequestsAction.OnTripClick -> emit(BookingRequestsEvent.NavigateToTripPassengers(action.tripId))
            is BookingRequestsAction.OnViewProfile ->
                emit(BookingRequestsEvent.NavigateToPassengerProfile(action.passengerId))
            is BookingRequestsAction.OnMessagePassenger -> emit(
                BookingRequestsEvent.NavigateToChat(
                    bookingId = action.booking.id,
                    tripId = action.booking.tripId,
                    passengerName = action.booking.passengerName,
                )
            )
            is BookingRequestsAction.OnDecision -> decisions.onAction(action.action)
            BookingRequestsAction.OnRetry -> load()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun load() {
        loadJob?.cancel()
        val driverId = authRepository.getCurrentUserId() ?: run {
            screenState.update { it.copy(isLoading = false, loadError = BookingError.NotAuthenticated) }
            return
        }
        screenState.update { it.copy(isLoading = true, loadError = null) }
        loadJob = viewModelScope.launch {
            val now = Clock.System.now().toEpochMilliseconds()
            // Shared so the seat listeners below don't open a second bookings listener.
            val bookings = bookingRepository.getOpenDriverBookings(driverId, departingAfter = now)
                .shareIn(this, SharingStarted.WhileSubscribed(), replay = 1)
            val freeSeats = bookings
                .map { list -> list.map { it.tripId }.toSet() }
                .distinctUntilChanged()
                .flatMapLatest(::freeSeatsOf)

            combine(bookings, freeSeats) { list, seats ->
                val pending = list.filter { it.status == BookingStatus.Pending }.groupByTrip(seats)
                val accepted = list.filter { it.status == BookingStatus.Confirmed }.groupByTrip(seats)
                pending to accepted
            }
                .catch { error -> screenState.update { it.copy(isLoading = false, loadError = error.toBookingError()) } }
                .collect { (pending, accepted) ->
                    screenState.update { it.copy(isLoading = false, pending = pending, accepted = accepted) }
                }
        }
    }

    /** Each trip's free seats, live, keyed by trip id. */
    private fun freeSeatsOf(tripIds: Set<String>): Flow<Map<String, Int>> =
        if (tripIds.isEmpty()) {
            flowOf(emptyMap())
        } else {
            combine(tripIds.map { id -> getTripAvailableSeats(id).map { id to it } }) { it.toMap() }
        }

    private fun emit(event: BookingRequestsEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}
