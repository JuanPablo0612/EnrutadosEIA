package com.juanpablo0612.carpool.presentation.trip.driver_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.use_case.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.use_case.GetDriverTripsUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.UpdateTripStatusUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetUserVehiclesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class DriverTripsViewModel(
    private val getDriverTripsUseCase: GetDriverTripsUseCase,
    private val getUserVehiclesUseCase: GetUserVehiclesUseCase,
    private val getTripAvailableSeatsUseCase: GetTripAvailableSeatsUseCase,
    private val updateTripStatusUseCase: UpdateTripStatusUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DriverTripsUiState())
    val state: StateFlow<DriverTripsUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<DriverTripsEvent>()
    val events: SharedFlow<DriverTripsEvent> = _events.asSharedFlow()

    private var tripsJob: Job? = null

    init {
        loadTrips()
    }

    private fun loadTrips() {
        tripsJob?.cancel()
        val userId = authRepository.getCurrentUserId() ?: run {
            _state.update { it.copy(isLoading = false) }
            return
        }

        tripsJob = viewModelScope.launch {
            combine(
                getDriverTripsUseCase(userId),
                getUserVehiclesUseCase(userId)
            ) { trips, vehicles -> Pair(trips, vehicles) }
                .flatMapLatest { (trips, vehicles) ->
                    val vehicleMap = vehicles.associateBy { it.id }
                    if (trips.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        combine(
                            trips.map { trip ->
                                getTripAvailableSeatsUseCase(trip.id, trip.seatCount)
                                    .map { available ->
                                        TripWithStats(
                                            trip = trip,
                                            occupiedSeats = trip.seatCount - available,
                                            vehicle = vehicleMap[trip.vehicleId]
                                        )
                                    }
                            }
                        ) { it.toList() }
                    }
                }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect { tripStats ->
                    val tab = _state.value.tab
                    val now = Clock.System.now().toEpochMilliseconds()
                    val filtered = tripStats
                        .filter { ts ->
                            if (tab is TripsTab.Upcoming) ts.trip.departureTime > now
                            else ts.trip.departureTime <= now
                        }
                        .sortedWith(
                            if (tab is TripsTab.Upcoming)
                                compareBy { it.trip.departureTime }
                            else
                                compareByDescending { it.trip.departureTime }
                        )
                    _state.update { it.copy(trips = filtered, isLoading = false) }
                }
        }
    }

    fun onAction(action: DriverTripsAction) {
        when (action) {
            is DriverTripsAction.SelectTab -> {
                _state.update { it.copy(tab = action.tab, isLoading = true) }
                loadTrips()
            }
            is DriverTripsAction.StartTrip -> updateStatus(action.tripId, TripStatus.InProgress)
            is DriverTripsAction.FinishTrip -> updateStatus(action.tripId, TripStatus.Completed)
            is DriverTripsAction.CancelTrip -> _state.update {
                it.copy(pendingCancelTripId = action.tripId)
            }
            is DriverTripsAction.ConfirmCancel -> {
                _state.update { it.copy(pendingCancelTripId = null) }
                updateStatus(action.tripId, TripStatus.Cancelled)
            }
            DriverTripsAction.DismissCancel -> _state.update { it.copy(pendingCancelTripId = null) }
            is DriverTripsAction.OpenTrip -> viewModelScope.launch {
                _events.emit(DriverTripsEvent.NavigateToTripDetail(action.tripId))
            }
            is DriverTripsAction.OpenPassengers -> viewModelScope.launch {
                _events.emit(DriverTripsEvent.NavigateToPassengers(action.tripId))
            }
            DriverTripsAction.PublishTrip -> viewModelScope.launch {
                _events.emit(DriverTripsEvent.NavigateToRoutesList)
            }
            DriverTripsAction.Refresh -> {
                _state.update { it.copy(isLoading = true) }
                loadTrips()
            }
            DriverTripsAction.OnBackClick -> viewModelScope.launch {
                _events.emit(DriverTripsEvent.NavigateBack)
            }
        }
    }

    private fun updateStatus(tripId: String, status: TripStatus) {
        viewModelScope.launch {
            updateTripStatusUseCase(tripId, status)
        }
    }
}
