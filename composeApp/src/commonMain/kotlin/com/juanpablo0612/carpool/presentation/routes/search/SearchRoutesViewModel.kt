package com.juanpablo0612.carpool.presentation.routes.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.use_case.GetAvailableTripsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchRoutesViewModel(
    getAvailableTripsUseCase: GetAvailableTripsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchRoutesUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SearchRoutesEvent>()
    val events: SharedFlow<SearchRoutesEvent> = _events.asSharedFlow()

    private val allTrips = MutableStateFlow<List<Trip>>(emptyList())
    private val query = MutableStateFlow("")

    init {
        getAvailableTripsUseCase()
            .onEach { trips ->
                allTrips.value = trips
                _uiState.update { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)

        combine(allTrips, query) { trips, q ->
            if (q.isBlank()) trips
            else trips.filter { trip ->
                trip.origin.name.contains(q, ignoreCase = true) ||
                    trip.destination.name.contains(q, ignoreCase = true)
            }
        }.onEach { filtered ->
            _uiState.update { it.copy(trips = filtered) }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: SearchRoutesAction) {
        when (action) {
            is SearchRoutesAction.OnQueryChanged -> {
                query.value = action.query
                _uiState.update { it.copy(query = action.query) }
            }
            is SearchRoutesAction.OnTripClick -> viewModelScope.launch {
                _events.emit(SearchRoutesEvent.NavigateToTripDetail(action.tripId))
            }
        }
    }
}
