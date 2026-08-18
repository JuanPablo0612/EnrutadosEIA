package com.juanpablo0612.carpool.presentation.place.picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.service.LocationService
import com.juanpablo0612.carpool.presentation.place.add.components.LocationPermissionRequester
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
) : ViewModel() {

    private val _state = MutableStateFlow(
        MapPickerUiState(pickedCoordinates = Coordinates(initialLatitude, initialLongitude))
    )
    val state: StateFlow<MapPickerUiState> = _state.asStateFlow()

    fun onPinDragged(coordinates: Coordinates) {
        _state.update { it.copy(pickedCoordinates = coordinates) }
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
                        isLoadingLocation = false,
                        isMyLocationEnabled = true,
                    )
                }
            } else {
                _state.update { it.copy(isLoadingLocation = false) }
            }
        }
    }
}
