package com.juanpablo0612.carpool.presentation.places.selector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.places.service.LocationService
import com.juanpablo0612.carpool.domain.places.service.PlacesSearchService
import com.juanpablo0612.carpool.domain.places.use_case.GetSavedPlacesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class PlaceSelectorEvent {
    data class PlaceSelected(val place: Place) : PlaceSelectorEvent()
    data object NavigateToAddPlace : PlaceSelectorEvent()
    data object Dismiss : PlaceSelectorEvent()
}

class PlaceSelectorViewModel(
    mode: String,
    private val getSavedPlacesUseCase: GetSavedPlacesUseCase,
    private val locationService: LocationService,
    private val placesSearchService: PlacesSearchService,
) : ViewModel() {

    private val _state = MutableStateFlow(
        PlaceSelectorUiState(mode = PlaceSelectorMode.fromString(mode))
    )
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<PlaceSelectorEvent>()
    val events: SharedFlow<PlaceSelectorEvent> = _events.asSharedFlow()

    private var getPlacesJob: Job? = null
    private var searchJob: Job? = null

    init {
        loadSavedPlaces()
    }

    private fun loadSavedPlaces() {
        getPlacesJob?.cancel()
        getPlacesJob = viewModelScope.launch {
            getSavedPlacesUseCase()
                .onEach { places ->
                    _state.update { it.copy(savedPlaces = places) }
                }
                .collect()
        }
    }

    fun onAction(action: PlaceSelectorAction) {
        when (action) {
            is PlaceSelectorAction.OnQueryChange -> handleQueryChange(action.query)
            PlaceSelectorAction.UseCurrentLocation -> resolveCurrentLocation()
            PlaceSelectorAction.RequestLocationPermission -> {
                _state.update { it.copy(locationPermissionGranted = false) }
            }
            is PlaceSelectorAction.OnSuggestionSelected -> selectSuggestion(action.suggestion)
            is PlaceSelectorAction.OnPlaceSelected -> viewModelScope.launch {
                _events.emit(PlaceSelectorEvent.PlaceSelected(action.place))
            }
            PlaceSelectorAction.OnAddPlace -> viewModelScope.launch {
                _events.emit(PlaceSelectorEvent.NavigateToAddPlace)
            }
            PlaceSelectorAction.OnDismiss -> viewModelScope.launch {
                _state.update { it.copy(searchQuery = "", searchResults = emptyList()) }
                _events.emit(PlaceSelectorEvent.Dismiss)
            }
        }
    }

    private fun handleQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                delay(300)
                _state.update { it.copy(isSearching = true) }
                val results = placesSearchService.search(query)
                _state.update { it.copy(searchResults = results, isSearching = false) }
            }
        } else {
            _state.update { it.copy(searchResults = emptyList()) }
        }
    }

    private fun resolveCurrentLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isResolvingLocation = true) }
            val coords = locationService.getCurrentCoordinates()
            if (coords == null) {
                _state.update { it.copy(isResolvingLocation = false, locationPermissionGranted = false) }
                return@launch
            }
            val address = placesSearchService.reverseGeocode(coords) ?: ""
            val place = Place(
                id = "current_location",
                name = "Mi ubicación actual",
                address = address,
                latitude = coords.latitude,
                longitude = coords.longitude,
            )
            _state.update { it.copy(currentLocation = place, isResolvingLocation = false) }
            _events.emit(PlaceSelectorEvent.PlaceSelected(place))
        }
    }

    private fun selectSuggestion(suggestion: com.juanpablo0612.carpool.domain.places.model.AutocompleteSuggestion) {
        viewModelScope.launch {
            val place = Place(
                id = "${suggestion.latitude}_${suggestion.longitude}",
                name = suggestion.primaryText,
                address = suggestion.fullAddress,
                latitude = suggestion.latitude,
                longitude = suggestion.longitude,
            )
            _events.emit(PlaceSelectorEvent.PlaceSelected(place))
        }
    }
}
