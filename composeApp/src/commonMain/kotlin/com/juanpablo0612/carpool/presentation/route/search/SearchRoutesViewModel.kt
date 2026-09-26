package com.juanpablo0612.carpool.presentation.route.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.rating.repository.RatingRepository
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripMatch
import com.juanpablo0612.carpool.domain.trip.model.TripSearchCriteria
import com.juanpablo0612.carpool.domain.trip.usecase.GetAvailableTripsUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.MatchTripsUseCase
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import com.juanpablo0612.carpool.presentation.trip.TripError
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchRoutesViewModel(
    private val getAvailableTripsUseCase: GetAvailableTripsUseCase,
    private val matchTripsUseCase: MatchTripsUseCase,
    private val vehicleRepository: VehicleRepository,
    private val authRepository: AuthRepository,
    private val ratingRepository: RatingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchRoutesUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SearchRoutesEvent>()
    val events: SharedFlow<SearchRoutesEvent> = _events.asSharedFlow()

    private var allTrips: List<Trip> = emptyList()
    private var tripsJob: Job? = null
    private var searchJob: Job? = null

    // Enrichment caches, touched only from viewModelScope (main dispatcher). Failures aren't
    // cached, so a refresh retries them.
    private val profileCache = mutableMapOf<String, PublicProfile>()
    private val ratingCache = mutableMapOf<String, Double?>()
    private val vehicleCache = mutableMapOf<String, Vehicle>()

    init {
        observeTrips()
    }

    private fun observeTrips() {
        tripsJob?.cancel()
        var isFirstEmission = true
        tripsJob = getAvailableTripsUseCase()
            .onEach { trips ->
                allTrips = trips
                _uiState.update { it.copy(isLoading = false, loadError = null) }
                // The default destination is a campus, so show matching trips right away
                // instead of an empty prompt.
                if (isFirstEmission) {
                    isFirstEmission = false
                    search()
                }
            }
            .catch { _uiState.update { it.copy(isLoading = false, loadError = TripError.Unknown) } }
            .launchIn(viewModelScope)
    }

    fun onAction(action: SearchRoutesAction) {
        when (action) {
            SearchRoutesAction.OnPickOrigin ->
                _uiState.update { it.copy(selectionTarget = SearchPlaceTarget.Origin) }

            SearchRoutesAction.OnPickDestination ->
                _uiState.update { it.copy(selectionTarget = SearchPlaceTarget.Destination) }

            is SearchRoutesAction.OnPlaceSelected -> updateAndResearch { state ->
                when (state.selectionTarget) {
                    SearchPlaceTarget.Origin -> state.copy(origin = action.place, selectionTarget = null)
                    SearchPlaceTarget.Destination -> state.copy(destination = action.place, selectionTarget = null)
                    null -> state
                }
            }

            SearchRoutesAction.OnCancelPlaceSelection ->
                _uiState.update { it.copy(selectionTarget = null) }

            SearchRoutesAction.OnClearOrigin -> updateAndResearch { it.copy(origin = null) }

            SearchRoutesAction.OnClearDestination -> updateAndResearch { it.copy(destination = null) }

            is SearchRoutesAction.OnCampusPreset -> updateAndResearch { state ->
                applyCampusPreset(state, action.campus, action.asOrigin)
            }

            SearchRoutesAction.OnSwapPlaces ->
                updateAndResearch { it.copy(origin = it.destination, destination = it.origin) }

            is SearchRoutesAction.OnDateTimeChanged -> updateAndResearch {
                it.copy(
                    selectedEpochMs = action.epochMs,
                    toleranceMinutes = action.toleranceMinutes,
                    showDateTimeSheet = false
                )
            }

            SearchRoutesAction.OnSearchClick -> search()

            is SearchRoutesAction.OnFiltersChanged ->
                updateAndResearch { it.copy(filters = action.filters, showFiltersSheet = false) }

            SearchRoutesAction.OnShowFilters -> _uiState.update { it.copy(showFiltersSheet = true) }

            SearchRoutesAction.OnDismissFilters -> _uiState.update { it.copy(showFiltersSheet = false) }

            SearchRoutesAction.OnShowDateTimeSheet -> _uiState.update { it.copy(showDateTimeSheet = true) }

            SearchRoutesAction.OnDismissDateTimeSheet -> _uiState.update { it.copy(showDateTimeSheet = false) }

            is SearchRoutesAction.OnWidenRadius -> updateAndResearch {
                it.copy(filters = it.filters.copy(maxWalkMeters = action.meters))
            }

            SearchRoutesAction.OnSearchAnyTime -> updateAndResearch { it.copy(selectedEpochMs = null) }

            is SearchRoutesAction.OnTripClick -> viewModelScope.launch {
                _events.emit(SearchRoutesEvent.NavigateToTripDetail(action.tripId))
            }

            SearchRoutesAction.Refresh -> {
                profileCache.clear()
                ratingCache.clear()
                vehicleCache.clear()
                search(isRefresh = true)
            }

            SearchRoutesAction.RetryLoad -> {
                _uiState.update { it.copy(isLoading = true, loadError = null) }
                observeTrips()
            }
        }
    }

    /** Applies [transform] and, once the user has searched, re-runs the search with the change. */
    private fun updateAndResearch(transform: (SearchRoutesUiState) -> SearchRoutesUiState) {
        _uiState.update(transform)
        if (_uiState.value.hasSearched) search()
    }

    private fun applyCampusPreset(state: SearchRoutesUiState, campus: Place, asOrigin: Boolean): SearchRoutesUiState {
        val current = if (asOrigin) state.origin else state.destination
        val alreadySelected = current?.id == campus.id
        return if (asOrigin) {
            state.copy(
                origin = if (alreadySelected) null else campus,
                // Campus to campus isn't a carpool trip; picking a campus on one end frees the other.
                destination = if (!alreadySelected && state.destination?.isCampusPreset == true) null else state.destination,
            )
        } else {
            state.copy(
                destination = if (alreadySelected) null else campus,
                origin = if (!alreadySelected && state.origin?.isCampusPreset == true) null else state.origin,
            )
        }
    }

    private fun search(isRefresh: Boolean = false) {
        val state = _uiState.value
        if (state.isLoading || state.loadError != null) {
            _uiState.update { it.copy(isRefreshing = false) }
            return
        }
        _uiState.update { if (isRefresh) it.copy(isRefreshing = true) else it.copy(isSearching = true) }

        val criteria = TripSearchCriteria(
            origin = state.origin,
            destination = state.destination,
            departureAroundEpochMs = state.selectedEpochMs,
            toleranceMinutes = state.toleranceMinutes,
            maxWalkMeters = state.filters.maxWalkMeters,
            maxContribution = state.filters.maxContribution,
        )
        val trips = allTrips

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val matches = matchTripsUseCase(trips, criteria)
            val relaxation = if (matches.isEmpty()) matchTripsUseCase.suggestRelaxation(trips, criteria) else null
            val results = enrich(matches)
            _uiState.update {
                it.copy(
                    results = results,
                    relaxation = relaxation,
                    isSearching = false,
                    isRefreshing = false,
                    hasSearched = true,
                )
            }
        }
    }

    /**
     * Loads driver profile, rating and vehicle for each match. Each distinct driver/vehicle is
     * fetched once, in parallel, and cached; a failed fetch degrades to a missing value and never
     * fails the search.
     */
    private suspend fun enrich(matches: List<TripMatch>): List<TripResult> = coroutineScope {
        val driverIds = matches.map { it.trip.driverId }.distinct()
        val vehicleIds = matches.map { it.trip.vehicleId }.filter { it.isNotBlank() }.distinct()

        val profiles = driverIds.filterNot(profileCache::containsKey).map { id ->
            async { id to authRepository.getPublicProfile(id).getOrNull() }
        }
        val ratings = driverIds.filterNot(ratingCache::containsKey).map { id ->
            async { id to ratingRepository.getUserAverageRating(id) }
        }
        val vehicles = vehicleIds.filterNot(vehicleCache::containsKey).map { id ->
            async { id to vehicleRepository.getVehicleById(id).getOrNull() }
        }

        profiles.awaitAll().forEach { (id, profile) -> if (profile != null) profileCache[id] = profile }
        ratings.awaitAll().forEach { (id, result) -> result.onSuccess { ratingCache[id] = it } }
        vehicles.awaitAll().forEach { (id, vehicle) -> if (vehicle != null) vehicleCache[id] = vehicle }

        matches.map { match ->
            TripResult(
                trip = match.trip,
                vehicle = vehicleCache[match.trip.vehicleId],
                availableSeats = match.availableSeats,
                driver = profileCache[match.trip.driverId],
                driverAverageRating = ratingCache[match.trip.driverId],
                pickup = match.pickup,
                dropoff = match.dropoff,
            )
        }
    }
}
