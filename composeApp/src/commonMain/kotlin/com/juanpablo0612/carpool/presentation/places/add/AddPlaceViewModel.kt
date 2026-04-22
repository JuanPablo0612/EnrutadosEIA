package com.juanpablo0612.carpool.presentation.places.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.places.use_case.CreatePlaceUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddPlaceViewModel(
    private val createPlaceUseCase: CreatePlaceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddPlaceUiState())
    val state: StateFlow<AddPlaceUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<AddPlaceEvent>()
    val events: SharedFlow<AddPlaceEvent> = _events.asSharedFlow()

    fun onAction(action: AddPlaceAction) {
        when (action) {
            is AddPlaceAction.OnNameChanged ->
                _state.update { it.copy(name = action.name, nameError = null) }
            is AddPlaceAction.OnAddressChanged ->
                _state.update { it.copy(address = action.address, addressError = null) }
            AddPlaceAction.OnSaveClick -> savePlace()
            AddPlaceAction.OnBackClick -> viewModelScope.launch {
                _events.emit(AddPlaceEvent.NavigateBack)
            }
        }
    }

    private fun savePlace() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(nameError = AddPlaceError.NameRequired) }
            return
        }
        if (s.address.isBlank()) {
            _state.update { it.copy(addressError = AddPlaceError.AddressRequired) }
            return
        }

        _state.update { it.copy(isLoading = true, generalError = null) }
        viewModelScope.launch {
            val place = Place(
                name = s.name.trim(),
                address = s.address.trim(),
                latitude = 0.0,
                longitude = 0.0
            )
            createPlaceUseCase(place)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.emit(AddPlaceEvent.PlaceSaved)
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, generalError = AddPlaceError.Unknown) }
                }
        }
    }
}
