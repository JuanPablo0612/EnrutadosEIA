package com.juanpablo0612.carpool.presentation.place.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.place.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.model.MapPointOfInterest
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.place.model.PlaceType
import com.juanpablo0612.carpool.domain.place.service.PlacesSearchService
import com.juanpablo0612.carpool.domain.place.usecase.CreatePlaceUseCase
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.place_type_gym
import enrutadoseia.composeapp.generated.resources.place_type_home
import enrutadoseia.composeapp.generated.resources.place_type_university
import enrutadoseia.composeapp.generated.resources.place_type_work
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
import org.jetbrains.compose.resources.getString
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class AddPlaceViewModel(
    private val createPlaceUseCase: CreatePlaceUseCase,
    private val placesSearchService: PlacesSearchService,
) : ViewModel() {

    private val _state = MutableStateFlow(AddPlaceUiState())
    val state: StateFlow<AddPlaceUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<AddPlaceEvent>()
    val events: SharedFlow<AddPlaceEvent> = _events.asSharedFlow()

    private var searchJob: Job? = null

    // Shared across every autocomplete keystroke and the details call for the picked
    // suggestion, then rotated — Google bills that whole sequence as one session instead
    // of pricing each autocomplete request separately.
    private var sessionToken = Uuid.random().toString()

    fun onAction(action: AddPlaceAction) {
        when (action) {
            is AddPlaceAction.SelectType -> selectType(action.type)
            is AddPlaceAction.OnNameChanged ->
                _state.update { it.copy(name = action.name, nameError = null) }
            is AddPlaceAction.OnAddressChanged -> handleAddressChange(action.text)
            is AddPlaceAction.SelectSuggestion -> selectSuggestion(action.suggestion)
            is AddPlaceAction.DragPin -> dragPin(action.to)
            is AddPlaceAction.SelectMapPoi -> selectMapPoi(action.poi)
            AddPlaceAction.OnSaveClick -> savePlace()
            AddPlaceAction.OnBackClick -> viewModelScope.launch {
                _events.emit(AddPlaceEvent.NavigateBack)
            }
            AddPlaceAction.PickOnMap -> viewModelScope.launch {
                _events.emit(AddPlaceEvent.NavigateToMapPicker)
            }
            is AddPlaceAction.OnMapPickResult -> dragPin(
                Coordinates(action.latitude, action.longitude),
                placeName = action.placeName,
            )
        }
    }

    private suspend fun defaultNameFor(type: PlaceType): String = when (type) {
        is PlaceType.Home -> getString(Res.string.place_type_home)
        is PlaceType.Work -> getString(Res.string.place_type_work)
        is PlaceType.Gym -> getString(Res.string.place_type_gym)
        is PlaceType.University -> getString(Res.string.place_type_university)
        is PlaceType.Other -> ""
    }

    private fun selectType(type: PlaceType) {
        viewModelScope.launch {
            val defaultName = defaultNameFor(type)
            val currentName = _state.value.name
            val previousDefault = _state.value.type?.let { defaultNameFor(it) } ?: ""
            val newName = if (currentName.isBlank() || currentName == previousDefault) defaultName else currentName
            _state.update { it.copy(type = type, name = newName) }
        }
    }

    private fun handleAddressChange(text: String) {
        _state.update { it.copy(address = text, hasManuallyEditedAddress = true) }
        searchJob?.cancel()
        if (text.length >= 2) {
            searchJob = viewModelScope.launch {
                delay(300)
                _state.update { it.copy(isSearchingAddress = true) }
                val suggestions = placesSearchService.search(text, sessionToken)
                _state.update { it.copy(autocompleteSuggestions = suggestions, isSearchingAddress = false) }
            }
        } else {
            _state.update { it.copy(autocompleteSuggestions = emptyList(), isSearchingAddress = false) }
        }
    }

    private fun selectSuggestion(suggestion: AutocompleteSuggestion) {
        if (_state.value.isResolvingSuggestion) return
        viewModelScope.launch {
            _state.update { it.copy(isResolvingSuggestion = true, generalError = null) }
            val coords = placesSearchService.resolvePlace(suggestion.placeId, sessionToken)
            if (coords == null) {
                _state.update {
                    it.copy(isResolvingSuggestion = false, generalError = AddPlaceError.SuggestionUnavailable)
                }
                return@launch
            }
            sessionToken = Uuid.random().toString()
            _state.update {
                it.copy(
                    // Only fill the name in if the user hasn't already set one themselves.
                    name = if (it.name.isBlank() && suggestion.primaryText.isNotBlank()) {
                        suggestion.primaryText
                    } else {
                        it.name
                    },
                    address = suggestion.fullAddress,
                    autocompleteSuggestions = emptyList(),
                    coordinates = coords,
                    isResolvingSuggestion = false,
                )
            }
        }
    }

    private fun selectMapPoi(poi: MapPointOfInterest) {
        _state.update {
            it.copy(
                coordinates = poi.coordinates,
                // Only fill the name in if the user hasn't already set one themselves.
                name = if (it.name.isBlank() && poi.name.isNotBlank()) poi.name else it.name,
            )
        }
        viewModelScope.launch {
            // A tapped POI carries a real Google placeId, so prefer its formatted address over
            // reverse-geocoding the raw coordinate — more accurate for named places.
            val address = placesSearchService.getPlaceAddress(poi.placeId)
                ?: placesSearchService.reverseGeocode(poi.coordinates)
            if (!address.isNullOrBlank()) {
                _state.update { it.copy(address = address) }
            }
        }
    }

    private fun dragPin(to: Coordinates, placeName: String? = null) {
        _state.update {
            it.copy(
                coordinates = to,
                // Only fill the name in if the user hasn't already set one themselves.
                name = if (it.name.isBlank() && !placeName.isNullOrBlank()) placeName else it.name,
            )
        }
        viewModelScope.launch {
            val address = placesSearchService.reverseGeocode(to)
            if (!address.isNullOrBlank()) {
                // Don't clobber an address the user has typed/edited themselves (5).
                _state.update { if (it.hasManuallyEditedAddress) it else it.copy(address = address) }
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
