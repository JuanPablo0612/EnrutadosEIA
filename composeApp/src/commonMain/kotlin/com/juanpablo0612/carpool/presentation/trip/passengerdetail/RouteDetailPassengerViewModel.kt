package com.juanpablo0612.carpool.presentation.trip.passengerdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.usecase.CheckExistingBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.CreateBookingUseCase
import com.juanpablo0612.carpool.domain.trip.model.TripMeetingStop
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.presentation.booking.toBookingError
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val BOOKING_SENT_BANNER_DURATION_MS = 900L
private const val MAX_PASSENGER_MESSAGE_LENGTH = 140

/**
 * One listener on the trip document keeps the seats and status current; the driver's name, photo
 * and car come with it. The only other reads are the driver's profile (for the rating) and the
 * check for a request the passenger already made.
 */
class RouteDetailPassengerViewModel(
    private val tripId: String,
    meetingStop: TripMeetingStop?,
    private val tripRepository: TripRepository,
    private val createBookingUseCase: CreateBookingUseCase,
    private val checkExistingBookingUseCase: CheckExistingBookingUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RouteDetailPassengerUiState(meetingStop = meetingStop))
    val state: StateFlow<RouteDetailPassengerUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<RouteDetailPassengerEvent>()
    val events: SharedFlow<RouteDetailPassengerEvent> = _events.asSharedFlow()

    private var extrasLoaded = false

    init {
        observeTrip()
    }

    private fun observeTrip() {
        tripRepository.getTripByIdFlow(tripId)
            .onEach { trip ->
                if (trip == null) {
                    _state.update { it.copy(isLoading = false, loadFailed = true) }
                    return@onEach
                }
                val isOwner = authRepository.getCurrentUserId() == trip.driverId
                _state.update { it.copy(isLoading = false, loadFailed = false, trip = trip, isOwner = isOwner) }
                if (!extrasLoaded) {
                    extrasLoaded = true
                    loadExtras(driverId = trip.driverId, isOwner = isOwner)
                }
            }
            .catch { _state.update { it.copy(isLoading = false, loadFailed = true) } }
            .launchIn(viewModelScope)
    }

    /** One-off reads that don't need to follow the trip: the driver's rating and our own request. */
    private fun loadExtras(driverId: String, isOwner: Boolean) {
        viewModelScope.launch {
            // A failed profile read only hides the rating; it never fails the screen.
            val profile = authRepository.getPublicProfile(driverId).getOrNull()
            _state.update { it.copy(driver = profile) }
        }
        if (!isOwner) {
            viewModelScope.launch {
                // A failed probe falls back to "not yet requested": CreateBookingUseCase re-checks
                // for an existing booking before it creates one, so this cannot produce a duplicate.
                val alreadyRequested = checkExistingBookingUseCase(tripId).getOrDefault(false)
                _state.update { it.copy(alreadyRequested = alreadyRequested) }
            }
        }
    }

    fun onAction(action: RouteDetailPassengerAction) {
        when (action) {
            RouteDetailPassengerAction.OnBackClick -> emit(RouteDetailPassengerEvent.NavigateBack)
            RouteDetailPassengerAction.OnOpenConfirmSheet -> _state.update { it.copy(showConfirmSheet = true, error = null) }
            RouteDetailPassengerAction.OnDismissConfirmSheet -> _state.update { it.copy(showConfirmSheet = false) }
            is RouteDetailPassengerAction.OnPassengerMessageChanged ->
                _state.update { it.copy(passengerMessage = action.message.take(MAX_PASSENGER_MESSAGE_LENGTH)) }
            is RouteDetailPassengerAction.OnQuickMessage -> _state.update {
                val combined = listOf(it.passengerMessage.trim(), action.text).filter(String::isNotEmpty).joinToString(" ")
                it.copy(passengerMessage = combined.take(MAX_PASSENGER_MESSAGE_LENGTH))
            }
            RouteDetailPassengerAction.OnConfirmBookingRequest -> book()
            RouteDetailPassengerAction.OnOpenDriverProfile -> _state.value.trip?.let {
                emit(RouteDetailPassengerEvent.NavigateToDriverProfile(it.driverId))
            }
        }
    }

    private fun book() {
        val trip = _state.value.trip ?: return
        if (_state.value.isOwner || _state.value.isBooking) return
        _state.update { it.copy(isBooking = true, error = null) }
        viewModelScope.launch {
            createBookingUseCase(
                trip = trip,
                meetingStop = _state.value.meetingStop,
                passengerMessage = _state.value.passengerMessage,
            )
                .onSuccess {
                    _state.update {
                        it.copy(isBooking = false, showConfirmSheet = false, alreadyRequested = true, bookingRequestSent = true)
                    }
                    // Brief inline confirmation before navigating away, so "request sent"
                    // isn't only communicated by an unannounced screen change.
                    delay(BOOKING_SENT_BANNER_DURATION_MS)
                    _events.emit(RouteDetailPassengerEvent.NavigateToPassengerBookings)
                }
                .onFailure { throwable ->
                    // The sheet stays open so the error shows where the passenger acted.
                    _state.update { it.copy(isBooking = false, error = throwable.toBookingError()) }
                }
        }
    }

    private fun emit(event: RouteDetailPassengerEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}
