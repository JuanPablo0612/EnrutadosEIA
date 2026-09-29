package com.juanpablo0612.carpool.presentation.mytrips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.booking.usecase.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.presentation.session.UserSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * "Mis viajes" in one list. Three listeners, all scoped to the user: the trips they drive, their
 * bookings as a passenger, and the seat requests waiting on them. Everything each row shows comes
 * from those documents (bookings carry the driver's name and photo), so the list costs no reads
 * per item, and switching between Próximos and Historial or the role filter is in memory.
 */
class MyTripsViewModel(
    private val userSession: UserSession,
    private val tripRepository: TripRepository,
    private val bookingRepository: BookingRepository,
    private val cancelBookingUseCase: CancelBookingUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MyTripsUiState())
    val state: StateFlow<MyTripsUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<MyTripsEvent>()
    val events: SharedFlow<MyTripsEvent> = _events.asSharedFlow()

    private var dataJob: Job? = null

    init {
        viewModelScope.launch {
            userSession.user.filterNotNull().map { it.id }.distinctUntilChanged().collectLatest { userId ->
                dataJob?.cancel()
                dataJob = launch { observe(userId) }
            }
        }
    }

    private suspend fun observe(userId: String) {
        combine(
            tripRepository.getDriverTrips(userId),
            bookingRepository.getPassengerBookings(userId),
            bookingRepository.getDriverBookingRequests(userId),
        ) { trips, bookings, requests ->
            _state.update {
                it.copy(
                    isLoading = false,
                    error = it.error.takeUnless { error -> error == MyTripsError.LoadFailed },
                    items = trips.map(MyTripItem::Driving) + bookings.map(MyTripItem::Riding),
                    pendingRequestCount = requests.count { request -> request.status == BookingStatus.Pending },
                )
            }
        }
            .catch { _state.update { it.copy(isLoading = false, error = MyTripsError.LoadFailed) } }
            .collect {}
    }

    fun onAction(action: MyTripsAction) {
        when (action) {
            is MyTripsAction.OnSegmentSelected -> _state.update { it.copy(segment = action.segment) }
            is MyTripsAction.OnFilterSelected -> _state.update { it.copy(filter = action.filter) }
            is MyTripsAction.OnItemClick -> emit(
                when (val item = action.item) {
                    is MyTripItem.Driving -> MyTripsEvent.NavigateToPassengers(item.tripId)
                    is MyTripItem.Riding -> MyTripsEvent.NavigateToTripDetail(item.tripId)
                }
            )
            MyTripsAction.OnOpenRequests -> emit(MyTripsEvent.NavigateToRequests)
            MyTripsAction.OnSearchTrips -> emit(MyTripsEvent.NavigateToSearch)
            MyTripsAction.OnPublishTrip -> emit(MyTripsEvent.NavigateToPublish)
            is MyTripsAction.OnViewPassengers -> emit(MyTripsEvent.NavigateToPassengers(action.tripId))
            is MyTripsAction.OnContinueTrip -> emit(MyTripsEvent.NavigateToTracking(action.tripId))
            is MyTripsAction.OnStartTrip ->
                _state.update { it.copy(confirmation = MyTripsConfirmation.StartTrip(action.tripId)) }
            is MyTripsAction.OnCancelTrip ->
                _state.update { it.copy(confirmation = MyTripsConfirmation.CancelTrip(action.tripId)) }
            is MyTripsAction.OnCancelBooking -> _state.update {
                it.copy(
                    confirmation = MyTripsConfirmation.CancelBooking(
                        bookingId = action.item.booking.id,
                        isPending = action.item.booking.status == BookingStatus.Pending,
                    )
                )
            }
            is MyTripsAction.OnMessageDriver -> emit(
                MyTripsEvent.NavigateToChat(
                    bookingId = action.item.booking.id,
                    tripId = action.item.tripId,
                    otherPartyName = action.item.booking.driver.name,
                    isReadOnly = action.isReadOnly,
                )
            )
            is MyTripsAction.OnRateDriver -> emit(
                MyTripsEvent.NavigateToRating(
                    bookingId = action.item.booking.id,
                    tripId = action.item.tripId,
                    rateeId = action.item.booking.driverId,
                    rateeName = action.item.booking.driver.name,
                )
            )
            MyTripsAction.OnConfirm -> confirm()
            MyTripsAction.OnDismissConfirmation -> _state.update { it.copy(confirmation = null) }
            MyTripsAction.OnDismissError -> _state.update { it.copy(error = null) }
            MyTripsAction.OnRetry -> {
                val userId = userSession.user.value?.id ?: return
                _state.update { it.copy(isLoading = true, error = null) }
                dataJob?.cancel()
                dataJob = viewModelScope.launch { observe(userId) }
            }
        }
    }

    private fun confirm() {
        val confirmation = _state.value.confirmation ?: return
        _state.update { it.copy(confirmation = null, error = null) }
        viewModelScope.launch {
            when (confirmation) {
                is MyTripsConfirmation.StartTrip -> runBusy("trip_${confirmation.tripId}") {
                    tripRepository.updateTripStatus(confirmation.tripId, TripStatus.InProgress)
                        .onSuccess { _events.emit(MyTripsEvent.NavigateToTracking(confirmation.tripId)) }
                }
                is MyTripsConfirmation.CancelTrip -> runBusy("trip_${confirmation.tripId}") {
                    // The cancellation cascade to its bookings runs in Cloud Functions.
                    tripRepository.updateTripStatus(confirmation.tripId, TripStatus.Cancelled)
                }
                is MyTripsConfirmation.CancelBooking -> runBusy("booking_${confirmation.bookingId}") {
                    cancelBookingUseCase(confirmation.bookingId)
                }
            }
        }
    }

    /** Marks [key] busy while [block] runs; a failure surfaces as [MyTripsError.ActionFailed]. */
    private suspend fun runBusy(key: String, block: suspend () -> Result<Unit>) {
        _state.update { it.copy(busyKey = key) }
        val result = block()
        _state.update {
            it.copy(busyKey = null, error = if (result.isFailure) MyTripsError.ActionFailed else it.error)
        }
    }

    private fun emit(event: MyTripsEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}
