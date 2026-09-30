package com.juanpablo0612.carpool.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.preferences.repository.UserPreferencesRepository
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
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
import kotlin.time.Clock

/**
 * Inicio listens to four queries, all scoped to the signed-in user: the trips they drive, the
 * seat requests waiting on them, their vehicles and their bookings as a passenger, plus one
 * local preference (whether they hid the vehicle suggestion). It shows
 * counts and the next trip only; accepting or rejecting a request happens on the requests
 * screen, which reads the requesters' details on demand.
 */
class HomeViewModel(
    private val userSession: UserSession,
    private val tripRepository: TripRepository,
    private val bookingRepository: BookingRepository,
    private val vehicleRepository: VehicleRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    private var dataJob: Job? = null

    init {
        viewModelScope.launch {
            userSession.user.filterNotNull().collectLatest { user ->
                _state.update { it.copy(user = user) }
            }
        }
        viewModelScope.launch {
            userSession.user.filterNotNull().map { it.id }.distinctUntilChanged().collectLatest { userId ->
                _state.update { it.copy(isLoading = true, error = null) }
                dataJob?.cancel()
                dataJob = launch { loadData(userId) }
            }
        }
    }

    private suspend fun loadData(userId: String) {
        combine(
            tripRepository.getDriverTrips(userId),
            bookingRepository.getDriverBookingRequests(userId),
            vehicleRepository.getUserVehicles(userId),
            bookingRepository.getPassengerBookings(userId),
            userPreferencesRepository.isVehicleSuggestionDismissed(userId),
        ) { trips, requests, vehicles, passengerBookings, vehicleSuggestionDismissed ->
            val now = Clock.System.now().toEpochMilliseconds()
            val nextDrive = trips
                .filter { it.status == TripStatus.Active && it.departureTime > now }
                .minByOrNull { it.departureTime }
                ?.let(UpcomingTrip::Driving)
            val nextRide = passengerBookings
                .filter { it.status == BookingStatus.Confirmed && it.departureTime > now }
                .minByOrNull { it.departureTime }
                ?.let(UpcomingTrip::Riding)
            val upcoming = listOfNotNull(nextDrive, nextRide).sortedBy { it.departureTime }
            _state.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    nextUp = upcoming.getOrNull(0),
                    later = upcoming.getOrNull(1),
                    // getDriverBookingRequests is already scoped to pending requests.
                    pendingRequestCount = requests.count { request -> request.status == BookingStatus.Pending },
                    hasVehicles = vehicles.isNotEmpty(),
                    hasBookedBefore = passengerBookings.isNotEmpty(),
                    vehicleSuggestionDismissed = vehicleSuggestionDismissed,
                    error = null,
                )
            }
        }
            .catch { _state.update { it.copy(isLoading = false, isRefreshing = false, error = HomeError.LoadFailed) } }
            .collect {}
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.SearchTrips -> emit(HomeEvent.NavigateToSearchTrips(shortcut = null))
            is HomeAction.SearchShortcutSelected -> emit(HomeEvent.NavigateToSearchTrips(action.shortcut))
            HomeAction.PublishTrip -> emit(HomeEvent.NavigateToPublishTrip)
            HomeAction.RegisterVehicle -> emit(HomeEvent.NavigateToRegisterVehicle)
            HomeAction.DismissVehicleSuggestion -> dismissVehicleSuggestion()
            HomeAction.OpenRequests -> emit(HomeEvent.NavigateToRequests)
            HomeAction.OpenNotifications -> emit(HomeEvent.NavigateToNotifications)
            HomeAction.Refresh -> refresh()
            is HomeAction.OpenTrip -> emit(HomeEvent.NavigateToTripDetail(action.tripId))
            is HomeAction.OpenPassengers -> emit(HomeEvent.NavigateToPassengers(action.tripId))
        }
    }

    private fun dismissVehicleSuggestion() {
        val userId = _state.value.user?.id ?: return
        // The preference flow feeding loadData hides it once the local write lands.
        viewModelScope.launch { userPreferencesRepository.dismissVehicleSuggestion(userId) }
    }

    private fun refresh() {
        val userId = _state.value.user?.id ?: return
        _state.update { it.copy(isRefreshing = true, error = null) }
        dataJob?.cancel()
        dataJob = viewModelScope.launch { loadData(userId) }
    }

    private fun emit(event: HomeEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}
