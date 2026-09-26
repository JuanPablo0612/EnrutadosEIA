package com.juanpablo0612.carpool.presentation.trip.publishweek

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.SlotStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.trip.usecase.GenerateRecurringTripSlotsUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.PublishRecurringTripsUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.WeekTripSettings
import com.juanpablo0612.carpool.domain.trip.validation.TripDraftValidator
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import com.juanpablo0612.carpool.presentation.trip.TripError
import com.juanpablo0612.carpool.presentation.trip.toTripError
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlin.time.Clock

class PublishWeekViewModel(
    private val routeId: String,
    private val routeRepository: RouteRepository,
    private val vehicleRepository: VehicleRepository,
    private val tripRepository: TripRepository,
    private val authRepository: AuthRepository,
    private val generateSlots: GenerateRecurringTripSlotsUseCase,
    private val publishRecurringTrips: PublishRecurringTripsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PublishWeekUiState())
    val state: StateFlow<PublishWeekUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<PublishWeekEvent>()
    val events: SharedFlow<PublishWeekEvent> = _events.asSharedFlow()

    private val timeZone = TimeZone.currentSystemDefault()
    private var loadJob: Job? = null

    init {
        load()
    }

    private fun load() {
        val uid = authRepository.getCurrentUserId() ?: run {
            _state.update { it.copy(isLoading = false, error = TripError.UserNotAuthenticated) }
            return
        }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val route = routeRepository.getRouteById(routeId).getOrElse {
                _state.update { it.copy(isLoading = false, error = TripError.TripNotFound) }
                return@launch
            }
            _state.update { it.copy(route = route, error = null) }
            // Trips and vehicles are live: publishing elsewhere or registering a vehicle updates
            // the slots and choices without losing the driver's ticks.
            combine(tripRepository.getDriverTrips(uid), vehicleRepository.getUserVehicles(uid)) { trips, vehicles ->
                trips to vehicles
            }
                .onEach { (trips, vehicles) -> applySnapshot(route, trips, vehicles) }
                .catch { _state.update { it.copy(isLoading = false, error = TripError.Unknown) } }
                .launchIn(this)
        }
    }

    private fun applySnapshot(route: Route, trips: List<Trip>, vehicles: List<Vehicle>) {
        val time = route.typicalDepartureTime
        val slots = if (time == null) emptyList() else {
            generateSlots(route.id, route.recurringDays, time, trips, Clock.System.now(), timeZone)
        }
        _state.update { s ->
            val firstLoad = s.isLoading
            // Defaults from the driver's latest trip on this route, as when publishing a single one.
            val latest = trips.filter { it.routeId == route.id }.maxByOrNull { it.departureTime }
            val vehicle = vehicles.firstOrNull { it.id == s.selectedVehicleId }
                ?: latest?.vehicleId?.let { id -> vehicles.firstOrNull { it.id == id } }
                ?: vehicles.firstOrNull { it.isPrimary }
                ?: vehicles.firstOrNull()
            val seats = when {
                vehicle == null -> s.seatCount
                firstLoad -> (latest?.seatCount ?: vehicle.seatsAvailable).coerceIn(1, vehicle.seatsAvailable)
                else -> s.seatCount.coerceIn(1, vehicle.seatsAvailable)
            }
            s.copy(
                isLoading = false,
                slots = slots,
                // Available days start ticked; conflicts have to be ticked on purpose.
                selectedDates = if (firstLoad) {
                    slots.filter { it.status == SlotStatus.Available }.map { it.date }.toSet()
                } else s.selectedDates.filterTo(mutableSetOf()) { date -> slots.any { it.date == date && it.status.isPublishable } },
                vehicles = vehicles,
                selectedVehicleId = vehicle?.id,
                seatCount = seats,
                contributionPerPassenger = if (firstLoad) latest?.contributionPerPassenger else s.contributionPerPassenger,
                message = if (firstLoad) latest?.messageToPassengers.orEmpty() else s.message,
            )
        }
    }

    fun onAction(action: PublishWeekAction) {
        when (action) {
            is PublishWeekAction.OnToggleDay -> _state.update {
                val selected = if (action.date in it.selectedDates) it.selectedDates - action.date else it.selectedDates + action.date
                it.copy(selectedDates = selected, error = null)
            }
            is PublishWeekAction.OnVehicleSelected -> _state.update { s ->
                val vehicle = s.vehicles.firstOrNull { it.id == action.vehicleId } ?: return@update s
                s.copy(selectedVehicleId = vehicle.id, seatCount = s.seatCount.coerceIn(1, vehicle.seatsAvailable))
            }
            is PublishWeekAction.OnSetSeats -> _state.update { it.copy(seatCount = action.count) }
            is PublishWeekAction.OnSetContribution -> _state.update {
                it.copy(contributionPerPassenger = action.pesos?.takeIf { p -> p > 0 })
            }
            is PublishWeekAction.OnSetMessage -> _state.update {
                it.copy(message = action.text.take(TripDraftValidator.MAX_MESSAGE_LENGTH))
            }
            PublishWeekAction.OnRegisterVehicleClick -> emit(PublishWeekEvent.NavigateToRegisterVehicle)
            PublishWeekAction.OnEditRouteClick -> emit(PublishWeekEvent.NavigateToRouteDetail(routeId))
            PublishWeekAction.OnPublishClick -> publish()
            PublishWeekAction.OnBackClick -> emit(PublishWeekEvent.NavigateBack)
            PublishWeekAction.OnRetry -> {
                _state.update { it.copy(isLoading = true, error = null) }
                load()
            }
        }
    }

    private fun publish() {
        val s = _state.value
        val route = s.route ?: return
        if (s.selectedCount == 0) {
            _state.update { it.copy(error = TripError.NothingSelected) }
            return
        }
        val vehicleId = s.selectedVehicleId ?: run {
            _state.update { it.copy(error = TripError.NoVehicleSelected) }
            return
        }
        _state.update { it.copy(isPublishing = true, error = null) }
        viewModelScope.launch {
            publishRecurringTrips(
                route = route,
                dates = s.selectedDates,
                settings = WeekTripSettings(vehicleId, s.seatCount, s.contributionPerPassenger, s.message),
                now = Clock.System.now(),
                timeZone = timeZone,
            ).onSuccess {
                _state.update { it.copy(isPublishing = false) }
                _events.emit(PublishWeekEvent.TripsPublished)
            }.onFailure { e ->
                _state.update { it.copy(isPublishing = false, error = e.toTripError()) }
            }
        }
    }

    private fun emit(event: PublishWeekEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}
