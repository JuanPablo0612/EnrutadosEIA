package com.juanpablo0612.carpool.presentation.routes.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.use_case.GetAvailableTripsUseCase
import com.juanpablo0612.carpool.domain.booking.use_case.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetDriverVehiclesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchRoutesViewModel(
    getAvailableTripsUseCase: GetAvailableTripsUseCase,
    private val getDriverVehiclesUseCase: GetDriverVehiclesUseCase,
    private val getTripAvailableSeatsUseCase: GetTripAvailableSeatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchRoutesUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SearchRoutesEvent>()
    val events: SharedFlow<SearchRoutesEvent> = _events.asSharedFlow()

    private val allTrips = MutableStateFlow<List<Trip>>(emptyList())

    init {
        getAvailableTripsUseCase()
            .onEach { trips ->
                allTrips.value = trips
                _uiState.update { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: SearchRoutesAction) {
        when (action) {
            is SearchRoutesAction.OnPickOrigin ->
                _uiState.update { it.copy(selectionTarget = "ORIGIN") }

            is SearchRoutesAction.OnPickDestination ->
                _uiState.update { it.copy(selectionTarget = "DESTINATION") }

            is SearchRoutesAction.OnPlaceSelected -> {
                val target = _uiState.value.selectionTarget
                _uiState.update { state ->
                    state.copy(
                        origin = if (target == "ORIGIN") action.place else state.origin,
                        destination = if (target == "DESTINATION") action.place else state.destination,
                        selectionTarget = null
                    )
                }
            }

            is SearchRoutesAction.OnCancelPlaceSelection ->
                _uiState.update { it.copy(selectionTarget = null) }

            is SearchRoutesAction.OnSwapPlaces ->
                _uiState.update { it.copy(origin = it.destination, destination = it.origin) }

            is SearchRoutesAction.OnDateTimeChanged ->
                _uiState.update {
                    it.copy(
                        selectedEpochMs = action.epochMs,
                        toleranceMinutes = action.toleranceMinutes,
                        showDateTimeSheet = false
                    )
                }

            is SearchRoutesAction.OnSearchClick -> search()

            is SearchRoutesAction.OnFiltersChanged ->
                _uiState.update { it.copy(filters = action.filters, showFiltersSheet = false) }

            is SearchRoutesAction.OnShowFilters ->
                _uiState.update { it.copy(showFiltersSheet = true) }

            is SearchRoutesAction.OnDismissFilters ->
                _uiState.update { it.copy(showFiltersSheet = false) }

            is SearchRoutesAction.OnShowDateTimeSheet ->
                _uiState.update { it.copy(showDateTimeSheet = true) }

            is SearchRoutesAction.OnDismissDateTimeSheet ->
                _uiState.update { it.copy(showDateTimeSheet = false) }

            is SearchRoutesAction.OnTripClick -> viewModelScope.launch {
                _events.emit(SearchRoutesEvent.NavigateToTripDetail(action.tripId))
            }
        }
    }

    private fun search() {
        val state = _uiState.value
        _uiState.update { it.copy(isSearching = true) }
        viewModelScope.launch {
            val filtered = allTrips.value.filter { trip ->
                val originMatch = state.origin == null ||
                    trip.origin.name.contains(state.origin.name, ignoreCase = true) ||
                    trip.origin.address.contains(state.origin.address, ignoreCase = true)
                val destMatch = state.destination == null ||
                    trip.destination.name.contains(state.destination.name, ignoreCase = true)

                val timeMatch = if (state.selectedEpochMs != null) {
                    val toleranceMs = state.toleranceMinutes * 60_000L
                    trip.departureTime in (state.selectedEpochMs - toleranceMs)..(state.selectedEpochMs + toleranceMs)
                } else true

                originMatch && destMatch && timeMatch
            }

            val results = filtered.mapNotNull { trip ->
                val vehicles = getDriverVehiclesUseCase(trip.driverId).first()
                val vehicle = vehicles.find { it.id == trip.vehicleId }
                val totalSeats = vehicle?.seatsAvailable ?: trip.seatCount
                val availableSeats = getTripAvailableSeatsUseCase(trip.id, totalSeats).first()

                val maxContrib = state.filters.maxContribution
                if (maxContrib != null) {
                    val contrib = trip.contributionPerPassenger ?: 0
                    if (contrib > maxContrib) return@mapNotNull null
                }

                if (state.filters.verifiedOnly) {
                    val isVerified = vehicle?.soatExpiresOn != null && vehicle.tecnomecanicaExpiresOn != null
                    if (!isVerified) return@mapNotNull null
                }

                TripResult(trip = trip, vehicle = vehicle, availableSeats = availableSeats)
            }

            _uiState.update { it.copy(results = results, isSearching = false, hasSearched = true) }
        }
    }
}
