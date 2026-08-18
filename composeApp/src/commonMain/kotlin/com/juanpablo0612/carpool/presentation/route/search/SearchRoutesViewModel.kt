package com.juanpablo0612.carpool.presentation.route.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.auth.usecase.GetUserPublicProfileUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.usecase.GetAvailableTripsUseCase
import com.juanpablo0612.carpool.domain.vehicle.usecase.GetUserVehiclesUseCase
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
    private val getUserVehiclesUseCase: GetUserVehiclesUseCase,
    private val getTripAvailableSeatsUseCase: GetTripAvailableSeatsUseCase,
    private val getUserPublicProfileUseCase: GetUserPublicProfileUseCase
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
                // An empty selected address must never match — trip.origin.address.contains("")
                // is true for every trip, which used to make this filter a no-op (3.11).
                val originMatch = state.origin == null ||
                        trip.origin.name.contains(state.origin.name, ignoreCase = true) ||
                        (state.origin.address.isNotBlank() &&
                                trip.origin.address.contains(state.origin.address, ignoreCase = true))
                val destMatch = state.destination == null ||
                        trip.destination.name.contains(state.destination.name, ignoreCase = true) ||
                        (state.destination.address.isNotBlank() &&
                                trip.destination.address.contains(
                                    state.destination.address,
                                    ignoreCase = true
                                ))

                val timeMatch = if (state.selectedEpochMs != null) {
                    val toleranceMs = state.toleranceMinutes * 60_000L
                    trip.departureTime in (state.selectedEpochMs - toleranceMs)..(state.selectedEpochMs + toleranceMs)
                } else true

                originMatch && destMatch && timeMatch
            }

            // One driver publishing several trips must cost one profile read, not one per trip:
            // fetch each distinct driverId exactly once and hand every trip the cached result.
            val driverProfiles = mutableMapOf<String, PublicProfile?>()
            for (driverId in filtered.map { it.driverId }.distinct()) {
                driverProfiles[driverId] = getUserPublicProfileUseCase(driverId)
                    .getOrNull() // a failed fetch degrades to null, it must never fail the search
            }

            val results = filtered.mapNotNull { trip ->
                val vehicles = getUserVehiclesUseCase(trip.driverId).first()
                val vehicle = vehicles.find { it.id == trip.vehicleId }
                val availableSeats = getTripAvailableSeatsUseCase(trip.id).first()

                val maxContrib = state.filters.maxContribution
                if (maxContrib != null) {
                    val contrib = trip.contributionPerPassenger ?: 0
                    if (contrib > maxContrib) return@mapNotNull null
                }

                TripResult(
                    trip = trip,
                    vehicle = vehicle,
                    availableSeats = availableSeats,
                    driver = driverProfiles[trip.driverId]
                )
            }

            _uiState.update { it.copy(results = results, isSearching = false, hasSearched = true) }
        }
    }
}
