package com.juanpablo0612.carpool.presentation.trip.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.trip.usecase.UpdateTripUseCase
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class EditTripViewModel(
    private val tripId: String,
    private val tripRepository: TripRepository,
    private val vehicleRepository: VehicleRepository,
    private val bookingRepository: BookingRepository,
    private val updateTripUseCase: UpdateTripUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(EditTripUiState())
    val state: StateFlow<EditTripUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<EditTripEvent>()
    val events: SharedFlow<EditTripEvent> = _events.asSharedFlow()

    init {
        load()
    }

    private fun load() {
        _state.update { it.copy(isLoading = true, loadError = null) }
        viewModelScope.launch {
            tripRepository.getTripById(tripId)
                .onSuccess { trip ->
                    val vehicle = vehicleRepository.getVehicleById(trip.vehicleId).getOrNull()
                    // Without the bookings no stop shows as locked; UpdateTripUseCase still
                    // refuses to drop one in use, so a failed read only loses the early warning.
                    val bookings = bookingRepository.getOpenBookingsForTrip(tripId, trip.driverId)
                        .catch { emit(emptyList()) }
                        .first()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            trip = trip,
                            vehicle = vehicle,
                            seatCount = trip.seatCount,
                            stops = StopsDraft.of(trip),
                            stopUsers = stopUsersByName(bookings),
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, loadError = TripError.TripNotFound) }
                }
        }
    }

    fun onAction(action: EditTripAction) {
        when (action) {
            EditTripAction.OnBackClick -> if (_state.value.isDirty) {
                _state.update { it.copy(showDiscardConfirm = true) }
            } else {
                emit(EditTripEvent.NavigateBack)
            }
            EditTripAction.OnConfirmDiscard -> {
                _state.update { it.copy(showDiscardConfirm = false) }
                emit(EditTripEvent.NavigateBack)
            }
            EditTripAction.OnDismissDiscard -> _state.update { it.copy(showDiscardConfirm = false) }
            EditTripAction.OnRetry -> load()
            is EditTripAction.OnSetSeats -> _state.update { it.copy(seatCount = action.seats, error = null) }
            is EditTripAction.OnEditWaypointClick -> _state.update {
                if (it.isWaypointLocked(action.index)) it
                else it.copy(selectionTarget = SelectionTarget.EditWaypoint(action.index))
            }
            EditTripAction.OnAddWaypointClick -> _state.update { it.copy(selectionTarget = SelectionTarget.NewWaypoint) }
            is EditTripAction.OnRemoveWaypoint -> _state.update {
                if (it.isWaypointLocked(action.index)) it
                else it.copy(stops = it.stops.removeWaypoint(action.index), error = null)
            }
            is EditTripAction.OnPlaceSelected -> onPlaceSelected(action.place)
            EditTripAction.OnCancelSelection -> _state.update { it.copy(selectionTarget = null) }
            EditTripAction.OnSaveClick -> save()
        }
    }

    private fun onPlaceSelected(place: Place) {
        _state.update { s ->
            val target = s.selectionTarget ?: return@update s
            s.copy(stops = s.stops.apply(target, place), selectionTarget = null, error = null)
        }
    }

    private fun save() {
        val current = _state.value
        if (current.trip == null || current.isSaving) return
        if (!current.isDirty) {
            emit(EditTripEvent.NavigateBack)
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            updateTripUseCase(tripId, current.seatCount, current.stops.waypoints, Clock.System.now())
                .onSuccess {
                    _state.update { it.copy(isSaving = false) }
                    _events.emit(EditTripEvent.TripUpdated)
                }
                .onFailure { e ->
                    _state.update { it.copy(isSaving = false, error = e.toTripError()) }
                }
        }
    }

    private fun emit(event: EditTripEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}
