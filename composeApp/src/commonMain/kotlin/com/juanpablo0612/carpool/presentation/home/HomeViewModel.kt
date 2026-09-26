package com.juanpablo0612.carpool.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.booking.usecase.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.RejectBookingUseCase
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import com.juanpablo0612.carpool.presentation.booking.toBookingError
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

class HomeViewModel(
    private val userSession: UserSession,
    private val tripRepository: TripRepository,
    private val bookingRepository: BookingRepository,
    private val vehicleRepository: VehicleRepository,
    private val routeRepository: RouteRepository,
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
        val now = Clock.System.now().toEpochMilliseconds()
        val drivingFlow = combine(
            tripRepository.getDriverTrips(userId),
            bookingRepository.getDriverBookingRequests(userId),
            bookingRepository.getAllDriverBookings(userId),
            vehicleRepository.getUserVehicles(userId),
            routeRepository.getUserRoutes(userId),
        ) { trips, pendingBookings, allDriverBookings, vehicles, routes ->
            val monthStart = startOfCurrentMonth(now)
            DrivingSnapshot(
                nextTrip = trips
                    .filter { it.status == TripStatus.Active && it.departureTime > now }
                    .minByOrNull { it.departureTime },
                pendingRequests = pendingBookings.filter { it.status == BookingStatus.Pending },
                hasVehicles = vehicles.isNotEmpty(),
                hasRoutes = routes.isNotEmpty(),
                hasTrips = trips.isNotEmpty(),
                tripsThisMonth = trips.count { it.departureTime >= monthStart && it.status != TripStatus.Cancelled },
                // getDriverBookingRequests is scoped to PENDING, so Confirmed bookings are counted
                // over the full driver booking set.
                passengersThisMonth = allDriverBookings.count {
                    it.departureTime >= monthStart && it.status == BookingStatus.Confirmed
                },
            )
        }

        combine(drivingFlow, bookingRepository.getPassengerBookings(userId)) { driving, passengerBookings ->
            val nextBooking = passengerBookings
                .filter { it.status == BookingStatus.Confirmed && it.departureTime > now }
                .minByOrNull { it.departureTime }
            _state.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    nextTrip = driving.nextTrip,
                    nextBooking = nextBooking,
                    pendingRequests = driving.pendingRequests,
                    hasVehicles = driving.hasVehicles,
                    hasRoutes = driving.hasRoutes,
                    hasTrips = driving.hasTrips,
                    tripsThisMonth = driving.tripsThisMonth,
                    passengersThisMonth = driving.passengersThisMonth,
                    error = null,
                )
            }
        }
            .catch { _state.update { it.copy(isLoading = false, isRefreshing = false, error = HomeError.LoadFailed) } }
            .collect {}
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.CreateRoute -> emit(HomeEvent.NavigateToCreateRoute)
            HomeAction.RegisterVehicle -> emit(HomeEvent.NavigateToRegisterVehicle)
            HomeAction.PublishTrip -> emit(HomeEvent.NavigateToPublishTrip)
            HomeAction.ViewMyRoutes -> emit(HomeEvent.NavigateToRoutesList)
            HomeAction.ViewMyTrips -> emit(HomeEvent.NavigateToMyTrips(tab = null))
            HomeAction.OpenAllRequests -> emit(HomeEvent.NavigateToDriverBookingRequests)
            HomeAction.SearchTrips -> emit(HomeEvent.NavigateToSearchTrips)
            HomeAction.ViewSavedPlaces -> emit(HomeEvent.NavigateToSavedPlaces)
            HomeAction.Refresh -> handleRefresh()
            is HomeAction.AcceptRequest -> confirmBooking(action.bookingId)
            is HomeAction.OnRejectRequestClick ->
                _state.update { it.copy(pendingRejectBookingId = action.bookingId) }
            HomeAction.OnConfirmReject -> {
                val bookingId = _state.value.pendingRejectBookingId
                _state.update { it.copy(pendingRejectBookingId = null) }
                if (bookingId != null) rejectBooking(bookingId)
            }
            HomeAction.OnDismissRejectConfirm -> _state.update { it.copy(pendingRejectBookingId = null) }
            is HomeAction.OpenTrip -> emit(HomeEvent.NavigateToTripDetail(action.tripId))
            is HomeAction.OpenBooking -> emit(HomeEvent.NavigateToTripDetail(action.tripId))
            HomeAction.DismissBookingActionError -> _state.update { it.copy(error = null) }
        }
    }

    private fun handleRefresh() {
        val userId = _state.value.user?.id ?: return
        _state.update { it.copy(isRefreshing = true, error = null) }
        dataJob?.cancel()
        dataJob = viewModelScope.launch { loadData(userId) }
    }

    private fun confirmBooking(bookingId: String) {
        if (bookingId in _state.value.processingBookingIds) return
        val tripId = _state.value.pendingRequests.firstOrNull { it.id == bookingId }?.tripId ?: return
        _state.update { it.copy(processingBookingIds = it.processingBookingIds + bookingId) }
        viewModelScope.launch {
            confirmBookingUseCase(bookingId, tripId).onFailure { e ->
                _state.update { it.copy(error = HomeError.BookingAction(e.toBookingError())) }
            }
            _state.update { it.copy(processingBookingIds = it.processingBookingIds - bookingId) }
        }
    }

    private fun rejectBooking(bookingId: String) {
        if (bookingId in _state.value.processingBookingIds) return
        _state.update { it.copy(processingBookingIds = it.processingBookingIds + bookingId) }
        viewModelScope.launch {
            rejectBookingUseCase(bookingId).onFailure { e ->
                _state.update { it.copy(error = HomeError.BookingAction(e.toBookingError())) }
            }
            _state.update { it.copy(processingBookingIds = it.processingBookingIds - bookingId) }
        }
    }

    private fun emit(event: HomeEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}

private data class DrivingSnapshot(
    val nextTrip: Trip?,
    val pendingRequests: List<Booking>,
    val hasVehicles: Boolean,
    val hasRoutes: Boolean,
    val hasTrips: Boolean,
    val tripsThisMonth: Int,
    val passengersThisMonth: Int,
)
