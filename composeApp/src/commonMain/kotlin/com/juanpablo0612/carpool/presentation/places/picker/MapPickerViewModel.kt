package com.juanpablo0612.carpool.presentation.places.picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.places.model.Coordinates
import com.juanpablo0612.carpool.domain.places.service.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapPickerViewModel(
    initialLatitude: Double,
    initialLongitude: Double,
    private val locationService: LocationService,
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
