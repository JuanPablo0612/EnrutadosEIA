package com.juanpablo0612.carpool.presentation.place.picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.model.MapPointOfInterest
import com.juanpablo0612.carpool.domain.place.service.LocationService
import com.juanpablo0612.carpool.domain.place.service.PlacesSearchService
import com.juanpablo0612.carpool.presentation.place.add.components.LocationPermissionRequester
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapPickerViewModel(
    initialLatitude: Double,
    initialLongitude: Double,
    private val locationService: LocationService,
    private val locationPermissionRequester: LocationPermissionRequester,
    private val placesSearchService: PlacesSearchService,
) : ViewModel() {

    private val _state = MutableStateFlow(
        MapPickerUiState(pickedCoordinates = Coordinates(initialLatitude, initialLongitude))
    )
    val state: StateFlow<MapPickerUiState> = _state.asStateFlow()

    private var geocodeJob: Job? = null

    init {
        resolveAddress(_state.value.pickedCoordinates)
    }

    fun onPinDragged(coordinates: Coordinates) {
        // A manually dropped pin has no associated place name, unlike a tapped POI.
        _state.update { it.copy(pickedCoordinates = coordinates, pickedPlaceName = null) }
        resolveAddress(coordinates)
    }

    fun onPoiSelected(poi: MapPointOfInterest) {
        _state.update { it.copy(pickedCoordinates = poi.coordinates, pickedPlaceName = poi.name) }
        // A tapped POI carries a real Google placeId, so prefer its formatted address over
        // reverse-geocoding the raw coordinate — more accurate for named places.
        resolveAddress(poi.coordinates, poi.placeId)
    }

    fun onMyLocationClick() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingLocation = true) }
            // Request the OS permission explicitly so isMyLocationEnabled only ever reflects a
            // real grant, never an assumption (3.12) — GoogleMap throws a SecurityException
            // otherwise.
            val granted = locationPermissionRequester.requestPermission()
            if (!granted) {
                _state.update { it.copy(isLoadingLocation = false, isMyLocationEnabled = false) }
                return@launch
            }
            val coords = locationService.getCurrentCoordinates()
            if (coords != null) {
                _state.update {
                    it.copy(
                        pickedCoordinates = coords,
                        pickedPlaceName = null,
                        isLoadingLocation = false,
                        isMyLocationEnabled = true,
                    )
                }
                resolveAddress(coords)
            } else {
                _state.update { it.copy(isLoadingLocation = false) }
            }
        }
    }

    private fun resolveAddress(coordinates: Coordinates, placeId: String? = null) {
        geocodeJob?.cancel()
        geocodeJob = viewModelScope.launch {
            _state.update { it.copy(isResolvingAddress = true, resolvedAddress = null) }
            val address = placeId?.let { placesSearchService.getPlaceAddress(it) }
                ?: placesSearchService.reverseGeocode(coordinates)
            _state.update { it.copy(resolvedAddress = address, isResolvingAddress = false) }
        }
    }
}
