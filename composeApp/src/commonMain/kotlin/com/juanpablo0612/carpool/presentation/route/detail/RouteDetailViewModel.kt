package com.juanpablo0612.carpool.presentation.route.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository
import com.juanpablo0612.carpool.domain.route.usecase.DuplicateRouteUseCase
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.presentation.route.create.CreateRouteUiState
import com.juanpablo0612.carpool.presentation.route.create.SelectionTarget
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

private const val SAVED_BANNER_DURATION_MS = 900L

class RouteDetailViewModel(
    private val routeId: String,
    private val routeRepository: RouteRepository,
    private val tripRepository: TripRepository,
    private val duplicateRouteUseCase: DuplicateRouteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RouteDetailUiState())
    val state: StateFlow<RouteDetailUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<RouteDetailEvent>()
    val events: SharedFlow<RouteDetailEvent> = _events.asSharedFlow()

    init {
        loadRouteAndStats()
    }

    private fun loadRouteAndStats() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            routeRepository.getRouteById(routeId)
                .onSuccess { route ->
                    _state.update { it.copy(route = route, isLoading = false) }
                    launch {
                        tripRepository.getDriverTrips(route.driverId).collect { trips ->
                            val routeTrips = trips.filter { it.routeId == routeId }
                            _state.update {
                                it.copy(
                                    tripsPublished = routeTrips.size,
                                    lastUsedAt = routeTrips.maxOfOrNull { t -> t.departureTime }
                                        ?.let { ms -> Instant.fromEpochMilliseconds(ms) }
                                )
                            }
                        }
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, error = RouteDetailError.NotFound) }
                }
        }
    }

    fun onAction(action: RouteDetailAction) {
        when (action) {
            RouteDetailAction.OnBackClick -> viewModelScope.launch {
                _events.emit(RouteDetailEvent.NavigateBack)
            }
            RouteDetailAction.OnEditClick -> enterEditMode()
            RouteDetailAction.OnCancelEdit -> {
                if (_state.value.isDraftDirty) {
                    _state.update { it.copy(showDiscardEditConfirm = true) }
                } else {
                    _state.update { it.copy(isEditing = false, draft = null) }
                }
            }
            RouteDetailAction.OnConfirmDiscardEdit -> _state.update {
                it.copy(isEditing = false, draft = null, showDiscardEditConfirm = false)
            }
            RouteDetailAction.OnDismissDiscardEditConfirm -> _state.update { it.copy(showDiscardEditConfirm = false) }
            RouteDetailAction.OnSaveChangesClick -> saveChanges()
            RouteDetailAction.OnDeleteClick -> _state.update { it.copy(showDeleteConfirm = true) }
            RouteDetailAction.OnConfirmDelete -> deleteRoute()
            RouteDetailAction.OnDismissDelete -> _state.update { it.copy(showDeleteConfirm = false) }
            RouteDetailAction.OnPublishTripClick -> viewModelScope.launch {
                _events.emit(RouteDetailEvent.NavigateToCreateTrip(routeId))
            }
            RouteDetailAction.OnDuplicateClick -> duplicateRoute()
            RouteDetailAction.OnRetry -> loadRouteAndStats()

            // Draft editing
            is RouteDetailAction.OnNameChange -> updateDraft { it.copy(name = action.name) }
            is RouteDetailAction.OnToggleRecurringDay -> updateDraft { draft ->
                val days = draft.recurringDays.toMutableSet()
                if (action.day in days) days.remove(action.day) else days.add(action.day)
                draft.copy(recurringDays = days)
            }
            is RouteDetailAction.OnSetDepartureTime -> updateDraft { it.copy(typicalDepartureTime = action.time) }
            RouteDetailAction.OnOriginClick -> updateDraft { it.copy(selectionTarget = SelectionTarget.Origin) }
            RouteDetailAction.OnDestinationClick -> updateDraft { it.copy(selectionTarget = SelectionTarget.Destination) }
            is RouteDetailAction.OnEditWaypointClick -> updateDraft {
                it.copy(selectionTarget = SelectionTarget.EditWaypoint(action.index))
            }
            RouteDetailAction.OnAddWaypointClick -> updateDraft { it.copy(selectionTarget = SelectionTarget.NewWaypoint) }
            is RouteDetailAction.OnRemoveWaypoint -> updateDraft {
                it.copy(waypoints = it.waypoints.filterIndexed { i, _ -> i != action.index })
            }
            is RouteDetailAction.OnPlaceSelectedFromResult -> onDraftPlaceSelected(action.place)
            RouteDetailAction.OnCancelSelection -> updateDraft { it.copy(selectionTarget = null) }
        }
    }

    private fun enterEditMode() {
        val route = _state.value.route ?: return
        _state.update {
            it.copy(
                isEditing = true,
                draft = CreateRouteUiState(
                    name = route.name,
                    origin = route.origin,
                    destination = route.destination,
                    waypoints = route.waypoints,
                    recurringDays = route.recurringDays,
                    typicalDepartureTime = route.typicalDepartureTime,
                )
            )
        }
    }

    private fun saveChanges() {
        val draft = _state.value.draft ?: return
        val route = _state.value.route ?: return
        val origin = draft.origin ?: return
        val destination = draft.destination ?: return

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val updatedRoute = route.copy(
                name = draft.name,
                origin = origin,
                destination = destination,
                waypoints = draft.waypoints,
                recurringDays = draft.recurringDays,
                typicalDepartureTime = draft.typicalDepartureTime,
            )
            routeRepository.updateRoute(updatedRoute)
                .onSuccess {
                    _state.update { it.copy(isSaving = false, isSaved = true, route = updatedRoute) }
                    delay(SAVED_BANNER_DURATION_MS)
                    _state.update { it.copy(isSaved = false, isEditing = false, draft = null) }
                }
                .onFailure {
                    _state.update { it.copy(isSaving = false, error = RouteDetailError.SaveFailed) }
                }
        }
    }

    private fun deleteRoute() {
        _state.update { it.copy(showDeleteConfirm = false, isDeleting = true) }
        viewModelScope.launch {
            routeRepository.deleteRoute(routeId)
                .onSuccess {
                    _state.update { it.copy(isDeleting = false) }
                    _events.emit(RouteDetailEvent.NavigateBack)
                }
                .onFailure {
                    _state.update { it.copy(isDeleting = false, error = RouteDetailError.DeleteFailed) }
                }
        }
    }

    private fun duplicateRoute() {
        val route = _state.value.route ?: return
        _state.update { it.copy(isDuplicating = true) }
        viewModelScope.launch {
            duplicateRouteUseCase(route, nameOverride = "${route.name} (copia)")
                .onSuccess {
                    _state.update { it.copy(isDuplicating = false) }
                    _events.emit(RouteDetailEvent.NavigateBack)
                }
                .onFailure {
                    _state.update { it.copy(isDuplicating = false, error = RouteDetailError.DuplicateFailed) }
                }
        }
    }

    private fun onDraftPlaceSelected(place: Place) {
        updateDraft { draft ->
            val target = draft.selectionTarget ?: return@updateDraft draft
            when (target) {
                SelectionTarget.Origin -> draft.copy(origin = place, selectionTarget = null)
                SelectionTarget.Destination -> draft.copy(destination = place, selectionTarget = null)
                is SelectionTarget.EditWaypoint -> {
                    val updated = draft.waypoints.toMutableList()
                    updated[target.index] = place
                    draft.copy(waypoints = updated, selectionTarget = null)
                }
                SelectionTarget.NewWaypoint -> draft.copy(
                    waypoints = draft.waypoints + place,
                    selectionTarget = null
                )
            }
        }
    }

    private fun updateDraft(transform: (CreateRouteUiState) -> CreateRouteUiState) {
        _state.update { s -> s.copy(draft = s.draft?.let(transform)) }
    }
}
