package com.juanpablo0612.carpool.presentation.places.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.places.model.PlaceType
import com.juanpablo0612.carpool.domain.places.service.PlacesSearchService
import com.juanpablo0612.carpool.domain.places.use_case.CreatePlaceUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddPlaceViewModel(
    private val createPlaceUseCase: CreatePlaceUseCase,
    private val placesSearchService: PlacesSearchService,
) : ViewModel() {

    private val _state = MutableStateFlow(AddPlaceUiState())
    val state: StateFlow<AddPlaceUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<AddPlaceEvent>()
    val events: SharedFlow<AddPlaceEvent> = _events.asSharedFlow()

    private var searchJob: Job? = null

    fun onAction(action: AddPlaceAction) {
        when (action) {
            is AddPlaceAction.SelectType -> selectType(action.type)
            is AddPlaceAction.OnNameChanged ->
                _state.update { it.copy(name = action.name, nameError = null) }
            is AddPlaceAction.OnAddressChanged -> handleAddressChange(action.text)
            is AddPlaceAction.SelectSuggestion -> selectSuggestion(action.suggestion)
            is AddPlaceAction.DragPin -> dragPin(action.to)
            AddPlaceAction.OnSaveClick -> savePlace()
            AddPlaceAction.OnBackClick -> viewModelScope.launch {
                _events.emit(AddPlaceEvent.NavigateBack)
            }
        }
    }

    private fun selectType(type: PlaceType) {
        val defaultName = when (type) {
            is PlaceType.Home -> "Casa"
            is PlaceType.Work -> "Trabajo"
            is PlaceType.Gym -> "Gimnasio"
            is PlaceType.University -> "Universidad"
            is PlaceType.Other -> ""
        }
        val currentName = _state.value.name
        val previousDefault = _state.value.type?.let { prevType ->
            when (prevType) {
                is PlaceType.Home -> "Casa"
                is PlaceType.Work -> "Trabajo"
                is PlaceType.Gym -> "Gimnasio"
                is PlaceType.University -> "Universidad"
                else -> ""
            }
        } ?: ""
        val newName = if (currentName.isBlank() || currentName == previousDefault) defaultName else currentName
        _state.update { it.copy(type = type, name = newName) }
    }

    private fun handleAddressChange(text: String) {
        _state.update { it.copy(address = text) }
        searchJob?.cancel()
        if (text.length >= 2) {
            searchJob = viewModelScope.launch {
                delay(300)
                val suggestions = placesSearchService.search(text)
                _state.update { it.copy(autocompleteSuggestions = suggestions) }
            }
        } else {
            _state.update { it.copy(autocompleteSuggestions = emptyList()) }
        }
    }

    private fun selectSuggestion(suggestion: com.juanpablo0612.carpool.domain.places.model.AutocompleteSuggestion) {
        viewModelScope.launch {
            _state.update { it.copy(address = suggestion.fullAddress, autocompleteSuggestions = emptyList(), isGeocoding = true) }
            val coords = placesSearchService.getCoordinates(suggestion.placeId)
            _state.update { it.copy(coordinates = coords, isGeocoding = false) }
        }
    }

    private fun dragPin(to: com.juanpablo0612.carpool.domain.places.model.Coordinates) {
        _state.update { it.copy(coordinates = to) }
        viewModelScope.launch {
            val address = placesSearchService.reverseGeocode(to)
            if (!address.isNullOrBlank()) {
                _state.update { it.copy(address = address) }
            }
        }
    }

    private fun savePlace() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(nameError = AddPlaceError.NameRequired) }
            return
        }
        if (s.coordinates == null) {
            _state.update { it.copy(generalError = AddPlaceError.CoordinatesRequired) }
            return
        }
        _state.update { it.copy(isSaving = true, generalError = null) }
        viewModelScope.launch {
            val place = Place(
                name = s.name.trim(),
                address = s.address.trim(),
                latitude = s.coordinates.latitude,
                longitude = s.coordinates.longitude,
                type = s.type ?: PlaceType.Other,
            )
            createPlaceUseCase(place)
                .onSuccess {
                    _state.update { it.copy(isSaving = false) }
                    _events.emit(AddPlaceEvent.PlaceSaved)
                }
                .onFailure {
                    _state.update { it.copy(isSaving = false, generalError = AddPlaceError.Unknown) }
                }
        }
    }
}
