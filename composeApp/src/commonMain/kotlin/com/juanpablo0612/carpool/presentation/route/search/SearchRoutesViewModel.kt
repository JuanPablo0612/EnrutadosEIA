package com.juanpablo0612.carpool.presentation.route.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.CampusDirection
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripMeetingStop
import com.juanpablo0612.carpool.domain.trip.model.TripSearchCriteria
import com.juanpablo0612.carpool.domain.trip.usecase.GetAvailableTripsUseCase
import com.juanpablo0612.carpool.domain.trip.usecase.MatchTripsUseCase
import com.juanpablo0612.carpool.presentation.trip.TripError
import kotlinx.coroutines.Job
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

/**
 * Listens to the open trips once and matches them in memory: changing the direction, campus,
 * place or time re-runs the match without touching Firestore. The listener keeps results live,
 * so there is no pull-to-refresh.
 */
class SearchRoutesViewModel(
    initialCampusId: String?,
    initialFromCampus: Boolean,
    private val getAvailableTripsUseCase: GetAvailableTripsUseCase,
    private val matchTripsUseCase: MatchTripsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SearchRoutesUiState().let { default ->
            default.copy(
                // Unknown ids fall back to the default campus rather than failing the screen.
                campus = Place.campusPresets.firstOrNull { it.id == initialCampusId } ?: default.campus,
                direction = if (initialFromCampus) CampusDirection.FromCampus else CampusDirection.ToCampus,
            )
        }
    )
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SearchRoutesEvent>()
    val events: SharedFlow<SearchRoutesEvent> = _events.asSharedFlow()

    private var allTrips: List<Trip> = emptyList()
    private var tripsJob: Job? = null

    init {
        observeTrips()
    }

    private fun observeTrips() {
        tripsJob?.cancel()
        tripsJob = getAvailableTripsUseCase()
            .onEach { trips ->
                allTrips = trips
                _uiState.update { it.copy(isLoading = false, loadError = null) }
                // Every search has a campus, so there is always something to show: match on each
                // update of the open trips, including the first.
                search()
            }
            .catch { _uiState.update { it.copy(isLoading = false, loadError = TripError.Unknown) } }
            .launchIn(viewModelScope)
    }

    fun onAction(action: SearchRoutesAction) {
        when (action) {
            is SearchRoutesAction.OnDirectionChanged -> updateAndSearch { it.copy(direction = action.direction) }
            is SearchRoutesAction.OnCampusSelected -> updateAndSearch { it.copy(campus = action.campus) }
            SearchRoutesAction.OnPickPlace -> _uiState.update { it.copy(isPickingPlace = true) }
            is SearchRoutesAction.OnPlaceSelected ->
                updateAndSearch { it.copy(place = action.place, isPickingPlace = false) }
            SearchRoutesAction.OnCancelPlaceSelection -> _uiState.update { it.copy(isPickingPlace = false) }
            SearchRoutesAction.OnClearPlace -> updateAndSearch { it.copy(place = null) }
            SearchRoutesAction.OnShowDateTimeSheet -> _uiState.update { it.copy(showDateTimeSheet = true) }
            SearchRoutesAction.OnDismissDateTimeSheet -> _uiState.update { it.copy(showDateTimeSheet = false) }
            is SearchRoutesAction.OnDateTimeChanged -> updateAndSearch {
                it.copy(
                    selectedEpochMs = action.epochMs,
                    toleranceMinutes = action.toleranceMinutes,
                    showDateTimeSheet = false,
                )
            }
            SearchRoutesAction.OnSearchAnyTime -> updateAndSearch { it.copy(selectedEpochMs = null) }
            is SearchRoutesAction.OnTripClick -> viewModelScope.launch {
                val result = _uiState.value.results.firstOrNull { it.trip.id == action.tripId }
                _events.emit(SearchRoutesEvent.NavigateToTripDetail(action.tripId, result?.meetingStop()))
            }
            SearchRoutesAction.RetryLoad -> {
                _uiState.update { it.copy(isLoading = true, loadError = null) }
                observeTrips()
            }
        }
    }

    private fun updateAndSearch(transform: (SearchRoutesUiState) -> SearchRoutesUiState) {
        _uiState.update(transform)
        search()
    }

    /**
     * The stop that meets the passenger: where they get on going to campus, where they get off
     * leaving it. A campus end is not worth marking, so it is ignored.
     */
    private fun TripResult.meetingStop(): TripMeetingStop? {
        val pickupStop = pickup?.takeUnless { it.place.isCampusPreset }
        val dropoffStop = dropoff?.takeUnless { it.place.isCampusPreset }
        return when {
            pickupStop != null -> TripMeetingStop(pickupStop.pathIndex, isDropoff = false)
            dropoffStop != null -> TripMeetingStop(dropoffStop.pathIndex, isDropoff = true)
            else -> null
        }
    }

    /** Matching is pure and in memory, so it runs synchronously on each change. */
    private fun search() {
        val state = _uiState.value
        if (state.isLoading || state.loadError != null) return
        val criteria = TripSearchCriteria.forCampus(
            direction = state.direction,
            campus = state.campus,
            place = state.place,
            departureAroundEpochMs = state.selectedEpochMs,
            toleranceMinutes = state.toleranceMinutes,
        )
        val matches = matchTripsUseCase(allTrips, criteria)
        val relaxation = if (matches.isEmpty()) matchTripsUseCase.suggestRelaxation(allTrips, criteria) else null
        _uiState.update {
            it.copy(
                results = matches.map { match ->
                    TripResult(
                        trip = match.trip,
                        availableSeats = match.availableSeats,
                        pickup = match.pickup,
                        dropoff = match.dropoff,
                    )
                },
                relaxation = relaxation,
                hasSearched = true,
            )
        }
    }
}
