package com.juanpablo0612.carpool.presentation.trip.publish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.trip.usecase.GenerateRecurringTripSlotsUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.PublishTripUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.RouteTemplate
import com.juanpablo0612.carpool.domain.trip.validation.TripDraft
import com.juanpablo0612.carpool.domain.trip.validation.TripDraftValidator
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import com.juanpablo0612.carpool.presentation.place.stops.SelectionTarget
import com.juanpablo0612.carpool.presentation.place.stops.StopsDraft
import com.juanpablo0612.carpool.presentation.trip.TripError
import com.juanpablo0612.carpool.presentation.trip.toTripError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class PublishTripViewModel(
    private val routeId: String?,
    private val routeRepository: RouteRepository,
    private val vehicleRepository: VehicleRepository,
    private val tripRepository: TripRepository,
    private val authRepository: AuthRepository,
    private val publishTripUseCase: PublishTripUseCase,
    private val generateSlots: GenerateRecurringTripSlotsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PublishTripUiState())
    val state: StateFlow<PublishTripUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<PublishTripEvent>()
    val events: SharedFlow<PublishTripEvent> = _events.asSharedFlow()

    private val timeZone = TimeZone.currentSystemDefault()
    private val userId = authRepository.getCurrentUserId()

    init {
        val (date, time) = defaultDeparture(Clock.System.now())
        _state.update { it.copy(departureDate = date, departureTime = time) }
        observeVehicles()
        observeSavedRoutes()
        if (routeId != null) {
            viewModelScope.launch {
                routeRepository.getRouteById(routeId)
                    .onSuccess { linkRoute(it) }
                    .onFailure { _state.update { s -> s.copy(error = TripError.TripNotFound) } }
                _state.update { it.copy(isLoading = false) }
            }
        } else {
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun observeVehicles() {
        val uid = userId ?: return
        vehicleRepository.getUserVehicles(uid)
            .onEach { vehicles -> _state.update { it.withVehicles(vehicles) } }
            .catch { _state.update { it.copy(error = TripError.Unknown) } }
            .launchIn(viewModelScope)
    }

    private fun observeSavedRoutes() {
        val uid = userId ?: return
        routeRepository.getUserRoutes(uid)
            .onEach { routes -> _state.update { it.copy(savedRoutes = routes.sortedBy { r -> r.name.lowercase() }) } }
            .catch { /* the chips are a shortcut; the form works without them */ }
            .launchIn(viewModelScope)
    }

    /**
     * Selects a vehicle registered while the driver was away (the "register vehicle" detour),
     * otherwise keeps the current choice or defaults to the primary/first vehicle, and keeps the
     * seat count within the chosen vehicle's capacity.
     */
    private fun PublishTripUiState.withVehicles(vehicles: List<Vehicle>): PublishTripUiState {
        val newlyRegistered = vehicleIdsBeforeRegister?.let { before -> vehicles.firstOrNull { it.id !in before } }
        val selected = newlyRegistered
            ?: vehicles.firstOrNull { it.id == selectedVehicleId }
            ?: vehicles.firstOrNull { it.isPrimary }
            ?: vehicles.firstOrNull()
        val seats = when {
            selected == null -> seatCount
            newlyRegistered != null || selectedVehicleId == null -> selected.seatsAvailable
            else -> seatCount.coerceIn(1, selected.seatsAvailable)
        }
        return copy(
            vehicles = vehicles,
            selectedVehicleId = selected?.id,
            seatCount = seats,
            vehicleIdsBeforeRegister = if (newlyRegistered != null) null else vehicleIdsBeforeRegister,
            fieldErrors = fieldErrors - TripError.NoVehicleSelected,
        )
    }

    /** Pre-fills the form from a saved route and the driver's latest trip on it. */
    private suspend fun linkRoute(route: Route) {
        val driverTrips = userId?.let { uid ->
            runCatching { tripRepository.getDriverTrips(uid).first() }.getOrDefault(emptyList())
        }.orEmpty()
        val now = Clock.System.now()
        val time = route.typicalDepartureTime ?: _state.value.departureTime ?: defaultDeparture(now).second
        val date = generateSlots.nextOccurrence(route.id, route.recurringDays, time, driverTrips, now, timeZone)
        val latest: Trip? = driverTrips.filter { it.routeId == route.id }.maxByOrNull { it.departureTime }
        _state.update { s ->
            val vehicle = latest?.vehicleId?.let { id -> s.vehicles.firstOrNull { it.id == id } }
            val capacity = (vehicle ?: s.selectedVehicle)?.seatsAvailable
            val seats = latest?.seatCount?.let { if (capacity != null) it.coerceIn(1, capacity) else it } ?: s.seatCount
            s.copy(
                linkedRoute = route,
                stops = StopsDraft.of(route),
                departureDate = date,
                departureTime = time,
                selectedVehicleId = vehicle?.id ?: s.selectedVehicleId,
                seatCount = seats,
                contributionPerPassenger = latest?.contributionPerPassenger ?: s.contributionPerPassenger,
                message = latest?.messageToPassengers ?: s.message,
                saveAsRoute = false,
                fieldErrors = emptySet(),
                error = null,
            )
        }
    }

    fun onAction(action: PublishTripAction) {
        when (action) {
            is PublishTripAction.OnSavedRouteClick -> {
                val route = _state.value.savedRoutes.firstOrNull { it.id == action.routeId } ?: return
                if (_state.value.linkedRoute?.id == route.id) {
                    onAction(PublishTripAction.OnClearLinkedRoute)
                } else {
                    viewModelScope.launch { linkRoute(route) }
                }
            }
            PublishTripAction.OnClearLinkedRoute -> _state.update {
                it.copy(linkedRoute = null, stops = StopsDraft())
            }

            PublishTripAction.OnOriginClick -> selectTarget(SelectionTarget.Origin)
            PublishTripAction.OnDestinationClick -> selectTarget(SelectionTarget.Destination)
            PublishTripAction.OnAddWaypointClick -> selectTarget(SelectionTarget.NewWaypoint)
            is PublishTripAction.OnEditWaypointClick ->
                selectTarget(SelectionTarget.EditWaypoint(action.index))
            is PublishTripAction.OnRemoveWaypoint -> updateStops { it.removeWaypoint(action.index) }
            is PublishTripAction.OnPlaceSelected -> {
                val target = _state.value.selectionTarget ?: return
                updateStops { it.apply(target, action.place) }
                _state.update { it.copy(selectionTarget = null) }
            }
            PublishTripAction.OnCancelSelection -> _state.update { it.copy(selectionTarget = null) }
            PublishTripAction.OnReverseStops -> updateStops { it.reversed() }

            PublishTripAction.OnSelectToday -> setDate(today())
            PublishTripAction.OnSelectTomorrow -> setDate(today().plus(1, DateTimeUnit.DAY))
            is PublishTripAction.OnDateSelected -> setDate(action.date)
            is PublishTripAction.OnTimeSelected -> _state.update {
                it.copy(departureTime = action.time, showTimePicker = false, fieldErrors = it.fieldErrors - departureErrors)
            }
            PublishTripAction.OnShowDatePicker -> _state.update { it.copy(showDatePicker = true) }
            PublishTripAction.OnShowTimePicker -> _state.update { it.copy(showTimePicker = true) }
            PublishTripAction.OnDismissDatePicker -> _state.update { it.copy(showDatePicker = false) }
            PublishTripAction.OnDismissTimePicker -> _state.update { it.copy(showTimePicker = false) }

            is PublishTripAction.OnVehicleSelected -> _state.update { s ->
                val vehicle = s.vehicles.firstOrNull { it.id == action.vehicleId } ?: return@update s
                s.copy(
                    selectedVehicleId = vehicle.id,
                    seatCount = s.seatCount.coerceIn(1, vehicle.seatsAvailable),
                    fieldErrors = s.fieldErrors - TripError.SeatsOutOfRange - TripError.NoVehicleSelected,
                )
            }
            PublishTripAction.OnRegisterVehicleClick -> {
                _state.update { s -> s.copy(vehicleIdsBeforeRegister = s.vehicles.map { it.id }.toSet()) }
                viewModelScope.launch { _events.emit(PublishTripEvent.NavigateToRegisterVehicle) }
            }
            is PublishTripAction.OnSetSeats -> _state.update {
                it.copy(seatCount = action.count, fieldErrors = it.fieldErrors - TripError.SeatsOutOfRange)
            }
            is PublishTripAction.OnSetContribution -> _state.update {
                // A zero contribution means a free trip.
                it.copy(
                    contributionPerPassenger = action.pesos?.takeIf { p -> p > 0 },
                    fieldErrors = it.fieldErrors - TripError.ContributionOutOfRange,
                )
            }
            is PublishTripAction.OnSetMessage -> _state.update {
                it.copy(
                    message = action.text.take(TripDraftValidator.MAX_MESSAGE_LENGTH),
                    fieldErrors = it.fieldErrors - TripError.MessageTooLong,
                )
            }

            is PublishTripAction.OnToggleSaveAsRoute -> _state.update { it.copy(saveAsRoute = action.enabled) }
            is PublishTripAction.OnRouteNameChange -> _state.update {
                it.copy(routeName = action.name, fieldErrors = it.fieldErrors - TripError.RouteNameRequired)
            }
            is PublishTripAction.OnToggleRecurringDay -> _state.update {
                it.copy(recurringDays = if (action.day in it.recurringDays) it.recurringDays - action.day else it.recurringDays + action.day)
            }

            PublishTripAction.OnReviewClick -> review()
            PublishTripAction.OnConfirmPublish -> publish()
            PublishTripAction.OnDismissSummary -> _state.update { it.copy(showSummary = false) }
            PublishTripAction.OnBackClick -> {
                if (_state.value.isDirty) {
                    _state.update { it.copy(showDiscardConfirm = true) }
                } else {
                    viewModelScope.launch { _events.emit(PublishTripEvent.NavigateBack) }
                }
            }
            PublishTripAction.OnConfirmDiscard -> {
                _state.update { it.copy(showDiscardConfirm = false) }
                viewModelScope.launch { _events.emit(PublishTripEvent.NavigateBack) }
            }
            PublishTripAction.OnDismissDiscard -> _state.update { it.copy(showDiscardConfirm = false) }
        }
    }

    private fun selectTarget(target: SelectionTarget) {
        _state.update { it.copy(selectionTarget = target) }
    }

    private fun updateStops(transform: (StopsDraft) -> StopsDraft) {
        _state.update {
            it.copy(
                stops = transform(it.stops),
                fieldErrors = it.fieldErrors - TripError.OriginDestinationRequired - TripError.SameOriginDestination,
            )
        }
    }

    private fun setDate(date: LocalDate) {
        _state.update { it.copy(departureDate = date, showDatePicker = false, fieldErrors = it.fieldErrors - departureErrors) }
    }

    private fun review() {
        val s = _state.value
        val draft = s.toDraft() ?: return
        val errors = TripDraftValidator.validate(draft, Clock.System.now()).map { it.toTripError() }.toSet()
        _state.update { it.copy(fieldErrors = errors, error = null, showSummary = errors.isEmpty()) }
    }

    private fun publish() {
        val s = _state.value
        val draft = s.toDraft() ?: return
        val vehicleId = s.selectedVehicleId ?: return
        _state.update { it.copy(showSummary = false, isPublishing = true, error = null) }
        viewModelScope.launch {
            val routeToSave = if (s.isFromScratch && s.saveAsRoute) {
                RouteTemplate(s.routeName, s.recurringDays, s.departureTime)
            } else null
            publishTripUseCase(
                draft = draft,
                vehicleId = vehicleId,
                now = Clock.System.now(),
                existingRouteId = s.linkedRoute?.id ?: s.savedRouteIdPendingTrip,
                routeToSave = routeToSave,
            ).onSuccess {
                _state.update { it.copy(isPublishing = false) }
                _events.emit(PublishTripEvent.TripPublished)
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isPublishing = false,
                        savedRouteIdPendingTrip = (e as? AppException.TripException.RouteSavedTripFailed)?.routeId
                            ?: it.savedRouteIdPendingTrip,
                        fieldErrors = (e as? AppException.TripException.Invalid)?.errors?.map { v -> v.toTripError() }?.toSet()
                            ?: it.fieldErrors,
                        error = if (e is AppException.TripException.Invalid) null else e.toTripError(),
                    )
                }
            }
        }
    }

    private fun PublishTripUiState.toDraft(): TripDraft? {
        val date = departureDate ?: return null
        val time = departureTime ?: return null
        return TripDraft(
            origin = stops.origin,
            destination = stops.destination,
            waypoints = stops.waypoints,
            departure = LocalDateTime(date, time).toInstant(timeZone),
            vehicleCapacity = selectedVehicle?.seatsAvailable,
            seatCount = seatCount,
            contributionPerPassenger = contributionPerPassenger,
            message = message,
            routeNameToSave = routeName.takeIf { isFromScratch && saveAsRoute },
        )
    }

    private fun today(): LocalDate = Clock.System.now().toLocalDateTime(timeZone).date

    /** About an hour from now, on the next quarter hour. */
    private fun defaultDeparture(now: Instant): Pair<LocalDate, LocalTime> {
        val inAnHour = (now + 1.hours).toLocalDateTime(timeZone)
        val minutes = inAnHour.hour * 60 + inAnHour.minute
        val rounded = ((minutes + 14) / 15) * 15
        val date = if (rounded >= 24 * 60) inAnHour.date.plus(1, DateTimeUnit.DAY) else inAnHour.date
        val minuteOfDay = rounded % (24 * 60)
        return date to LocalTime(minuteOfDay / 60, minuteOfDay % 60)
    }

    private companion object {
        val departureErrors = setOf(TripError.DepartureTooSoon, TripError.DepartureTooFar)
    }
}
