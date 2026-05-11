package com.juanpablo0612.carpool.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.use_case.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetDriverBookingRequestsUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetPassengerBookingsUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.RejectBookingUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.GetUserRoutesUseCase
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.use_case.GetDriverTripsUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetUserVehiclesUseCase
import com.juanpablo0612.carpool.presentation.session.UserSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class HomeViewModel(
    private val userSession: UserSession,
    private val getDriverTripsUseCase: GetDriverTripsUseCase,
    private val getDriverBookingRequestsUseCase: GetDriverBookingRequestsUseCase,
    private val getPassengerBookingsUseCase: GetPassengerBookingsUseCase,
    private val getUserVehiclesUseCase: GetUserVehiclesUseCase,
    private val getUserRoutesUseCase: GetUserRoutesUseCase,
    private val confirmBookingUseCase: ConfirmBookingUseCase,
    private val rejectBookingUseCase: RejectBookingUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    private var dataJob: Job? = null

    init {
        viewModelScope.launch {
            combine(userSession.user, userSession.activeRole) { user, role -> user to role }
                .collect { (user, role) ->
                    if (user == null || role == null) return@collect
                    val isDualRole = user.isDriver && user.isPassenger
                    _state.update {
                        it.copy(
                            user = user,
                            role = role,
                            isDualRole = isDualRole,
                            isLoading = true,
                            error = null,
                        )
                    }
                    dataJob?.cancel()
                    dataJob = launch { loadData(userId = user.id, role = role) }
                }
        }
    }

    private suspend fun loadData(userId: String, role: UserRole) {
        val now = Clock.System.now().toEpochMilliseconds()
        when (role) {
            UserRole.Driver -> loadDriverData(userId, now)
            UserRole.Passenger -> loadPassengerData(userId, now)
        }
    }

    private suspend fun loadDriverData(userId: String, now: Long) {
        combine(
            getDriverTripsUseCase(userId),
            getDriverBookingRequestsUseCase(userId),
            getUserVehiclesUseCase(userId),
            getUserRoutesUseCase(userId),
        ) { trips, bookings, vehicles, routes ->
            val monthStart = startOfCurrentMonth(now)
            val nextTrip = trips
                .filter { it.status == TripStatus.Active && it.departureTime > now }
                .minByOrNull { it.departureTime }
            val pendingRequests = bookings.filter { it.status == BookingStatus.Pending }
            val tripsThisMonth = trips.count {
                it.departureTime >= monthStart && it.status != TripStatus.Cancelled
            }
            val passengersThisMonth = bookings.count {
                it.departureTime >= monthStart && it.status == BookingStatus.Confirmed
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    nextTrip = nextTrip,
                    pendingRequests = pendingRequests,
                    hasVehicles = vehicles.isNotEmpty(),
                    hasRoutes = routes.isNotEmpty(),
                    tripsThisMonth = tripsThisMonth,
                    passengersThisMonth = passengersThisMonth,
                    error = null,
                )
            }
        }
            .catch { e -> _state.update { it.copy(isLoading = false, isRefreshing = false, error = e.message) } }
            .collect {}
    }

    private suspend fun loadPassengerData(userId: String, now: Long) {
        getPassengerBookingsUseCase(userId)
            .onEach { bookings ->
                val nextBooking = bookings
                    .filter { it.status == BookingStatus.Confirmed && it.departureTime > now }
                    .minByOrNull { it.departureTime }
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        nextBooking = nextBooking,
                        error = null,
                    )
                }
            }
            .catch { e -> _state.update { it.copy(isLoading = false, isRefreshing = false, error = e.message) } }
            .collect {}
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.SwitchRole -> emit(HomeEvent.NavigateToSwitchRole)
            HomeAction.CreateRoute -> emit(HomeEvent.NavigateToCreateRoute)
            HomeAction.RegisterVehicle -> emit(HomeEvent.NavigateToRegisterVehicle)
            HomeAction.PublishTrip -> emit(HomeEvent.NavigateToRoutesList)
            HomeAction.ViewMyRoutes -> emit(HomeEvent.NavigateToRoutesList)
            HomeAction.ViewMyTrips -> emit(HomeEvent.NavigateToDriverTrips)
            HomeAction.OpenAllRequests -> emit(HomeEvent.NavigateToDriverBookingRequests)
            HomeAction.SearchTrips -> emit(HomeEvent.NavigateToSearchTrips)
            HomeAction.ViewMyBookings -> emit(HomeEvent.NavigateToPassengerBookings)
            HomeAction.ViewSavedPlaces -> emit(HomeEvent.NavigateToPassengerBookings)
            HomeAction.Refresh -> handleRefresh()
            is HomeAction.AcceptRequest -> confirmBooking(action.bookingId)
            is HomeAction.RejectRequest -> rejectBooking(action.bookingId)
            is HomeAction.OpenTrip -> emit(HomeEvent.NavigateToTripDetail(action.tripId))
            is HomeAction.OpenBooking -> emit(HomeEvent.NavigateToTripDetailPassenger(action.tripId))
        }
    }

    private fun handleRefresh() {
        val current = _state.value
        val userId = current.user?.id ?: return
        _state.update { it.copy(isRefreshing = true, error = null) }
        dataJob?.cancel()
        dataJob = viewModelScope.launch { loadData(userId, current.role) }
    }

    private fun confirmBooking(bookingId: String) {
        viewModelScope.launch {
            confirmBookingUseCase(bookingId).onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun rejectBooking(bookingId: String) {
        viewModelScope.launch {
            rejectBookingUseCase(bookingId).onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun emit(event: HomeEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}
