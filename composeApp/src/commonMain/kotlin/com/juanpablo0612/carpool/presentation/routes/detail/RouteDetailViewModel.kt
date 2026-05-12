package com.juanpablo0612.carpool.presentation.routes.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.routes.use_case.CreateRouteUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.DeleteRouteUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.GetRouteByIdUseCase
import com.juanpablo0612.carpool.domain.routes.use_case.UpdateRouteUseCase
import com.juanpablo0612.carpool.domain.trip.use_case.GetDriverTripsUseCase
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteUiState
import com.juanpablo0612.carpool.presentation.routes.create.SelectionTarget
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class RouteDetailViewModel(
    private val routeId: String,
    private val getRouteByIdUseCase: GetRouteByIdUseCase,
    private val updateRouteUseCase: UpdateRouteUseCase,
    private val deleteRouteUseCase: DeleteRouteUseCase,
    private val getDriverTripsUseCase: GetDriverTripsUseCase,
    private val createRouteUseCase: CreateRouteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RouteDetailUiState())
    val state: StateFlow<RouteDetailUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<RouteDetailEvent>()
    val events: SharedFlow<RouteDetailEvent> = _events.asSharedFlow()

    init {
        loadRouteAndStats()
    }

    private fun loadRouteAndStats() {
        viewModelScope.launch {
            getRouteByIdUseCase(routeId)
                .onSuccess { route ->
                    _state.update { it.copy(route = route, isLoading = false) }
                    launch {
                        getDriverTripsUseCase(route.driverId).collect { trips ->
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
                    _state.update { it.copy(isLoading = false, error = "Ruta no encontrada") }
                }
        }
    }

    fun onAction(action: RouteDetailAction) {
        when (action) {
            RouteDetailAction.OnBackClick -> viewModelScope.launch {
                _events.emit(RouteDetailEvent.NavigateBack)
            }
            RouteDetailAction.OnEditClick -> enterEditMode()
            RouteDetailAction.OnCancelEdit -> _state.update { it.copy(isEditing = false, draft = null) }
            RouteDetailAction.OnSaveChangesClick -> saveChanges()
            RouteDetailAction.OnDeleteClick -> _state.update { it.copy(showDeleteConfirm = true) }
            RouteDetailAction.OnConfirmDelete -> deleteRoute()
            RouteDetailAction.OnDismissDelete -> _state.update { it.copy(showDeleteConfirm = false) }
            RouteDetailAction.OnPublishTripClick -> viewModelScope.launch {
                _events.emit(RouteDetailEvent.NavigateToCreateTrip(routeId))
            }
            RouteDetailAction.OnDuplicateClick -> duplicateRoute()

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
                    typicalDepartureTime = route.typicalDepartureTime
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
                typicalDepartureTime = draft.typicalDepartureTime
            )
            updateRouteUseCase(updatedRoute)
                .onSuccess {
                    _state.update { it.copy(isSaving = false, isEditing = false, draft = null, route = updatedRoute) }
                }
                .onFailure {
                    _state.update { it.copy(isSaving = false, error = "No se pudo guardar") }
                }
        }
    }

    private fun deleteRoute() {
        _state.update { it.copy(showDeleteConfirm = false, isDeleting = true) }
        viewModelScope.launch {
            deleteRouteUseCase(routeId)
                .onSuccess {
                    _state.update { it.copy(isDeleting = false) }
                    _events.emit(RouteDetailEvent.NavigateBack)
                }
                .onFailure {
                    _state.update { it.copy(isDeleting = false, error = "No se pudo eliminar la ruta") }
                }
        }
    }

    private fun duplicateRoute() {
        val route = _state.value.route ?: return
        viewModelScope.launch {
            createRouteUseCase(route.copy(id = "", name = "${route.name} (copia)"))
                .onSuccess {
                    _events.emit(RouteDetailEvent.NavigateBack)
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
