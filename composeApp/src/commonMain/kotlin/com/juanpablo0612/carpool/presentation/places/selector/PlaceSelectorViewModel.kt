package com.juanpablo0612.carpool.presentation.places.selector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.places.use_case.CreatePlaceUseCase
import com.juanpablo0612.carpool.domain.places.use_case.GetSavedPlacesUseCase
import com.juanpablo0612.carpool.domain.places.use_case.SearchPlacesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlaceSelectorViewModel(
    private val getSavedPlacesUseCase: GetSavedPlacesUseCase,
    private val searchPlacesUseCase: SearchPlacesUseCase,
    private val createPlaceUseCase: CreatePlaceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PlaceSelectorState())
    val state = _state.asStateFlow()

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
            is PlaceSelectorAction.OnQueryChange -> {
                _state.update { it.copy(query = action.query) }
                searchJob?.cancel()
                if (action.query.isNotBlank()) {
                    searchJob = viewModelScope.launch {
                        delay(300)
                        _state.update { it.copy(isLoading = true) }
                        searchPlacesUseCase(action.query)
                            .onSuccess { results ->
                                _state.update { it.copy(searchResults = results, isLoading = false) }
                            }
                            .onFailure { error ->
                                _state.update { it.copy(error = error.message, isLoading = false) }
                            }
                    }
                } else {
                    _state.update { it.copy(searchResults = emptyList()) }
                }
            }
            is PlaceSelectorAction.OnSavePlace -> {
                viewModelScope.launch {
                    val place = Place(
                        id = "",
                        name = action.name,
                        address = action.address,
                        latitude = action.lat,
                        longitude = action.lng
                    )
                    createPlaceUseCase(place)
                        .onSuccess {
                            _state.update { it.copy(query = "") }
                        }
                }
            }
            PlaceSelectorAction.OnDismiss -> {
                _state.update { it.copy(query = "", searchResults = emptyList(), error = null) }
            }
            is PlaceSelectorAction.OnPlaceSelected -> {
                // Handled by navigation/UI
            }
        }
    }
}
