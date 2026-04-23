package com.juanpablo0612.carpool.presentation.places.selector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.places.use_case.GetSavedPlacesUseCase
import com.juanpablo0612.carpool.domain.places.use_case.SearchPlacesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaceSelectorViewModel(
    private val getSavedPlacesUseCase: GetSavedPlacesUseCase,
    private val searchPlacesUseCase: SearchPlacesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PlaceSelectorUiState())
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
                            .onFailure {
                                _state.update { it.copy(isLoading = false) }
                            }
                    }
                } else {
                    _state.update { it.copy(searchResults = emptyList()) }
                }
            }
            PlaceSelectorAction.OnDismiss -> {
                _state.update { it.copy(query = "", searchResults = emptyList()) }
            }
            is PlaceSelectorAction.OnPlaceSelected -> {
                // Handled by navigation/UI callback
            }
        }
    }
}
