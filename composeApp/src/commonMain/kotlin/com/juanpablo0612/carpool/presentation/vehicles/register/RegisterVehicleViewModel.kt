package com.juanpablo0612.carpool.presentation.vehicles.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicles.use_case.CreateVehicleUseCase
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterVehicleViewModel(
    private val createVehicleUseCase: CreateVehicleUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterVehicleUiState())
    val state: StateFlow<RegisterVehicleUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<RegisterVehicleEvent>()
    val events: SharedFlow<RegisterVehicleEvent> = _events.asSharedFlow()

    fun onAction(action: RegisterVehicleAction) {
        when (action) {
            is RegisterVehicleAction.OnBrandChanged ->
                _state.update { it.copy(brand = action.brand, error = null) }
            is RegisterVehicleAction.OnModelChanged ->
                _state.update { it.copy(model = action.model, error = null) }
            is RegisterVehicleAction.OnLicensePlateChanged ->
                _state.update { it.copy(licensePlate = action.licensePlate, error = null) }
            is RegisterVehicleAction.OnColorChanged ->
                _state.update { it.copy(color = action.color, error = null) }
            is RegisterVehicleAction.OnYearChanged ->
                _state.update { it.copy(year = action.year, error = null) }
            is RegisterVehicleAction.OnSeatsChanged ->
                _state.update { it.copy(seatsAvailable = action.seats, error = null) }
            is RegisterVehicleAction.OnPhotoSelected ->
                _state.update { it.copy(vehiclePhoto = action.photo, error = null) }
            RegisterVehicleAction.OnSaveClick -> registerVehicle()
            RegisterVehicleAction.OnBackClick -> viewModelScope.launch {
                _events.emit(RegisterVehicleEvent.NavigateBack)
            }
        }
    }

    private fun registerVehicle() {
        val s = _state.value

        val validationError = validate(s)
        if (validationError != null) {
            _state.update { it.copy(error = validationError) }
            return
        }

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            _state.update { it.copy(error = RegisterVehicleError.UserNotAuthenticated) }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val photoBytes = s.vehiclePhoto!!.readBytes()

            val vehicle = Vehicle(
                driverId = userId,
                brand = s.brand.trim(),
                model = s.model.trim(),
                licensePlate = s.licensePlate.trim().uppercase(),
                color = s.color.trim(),
                year = s.year.trim().toInt(),
                seatsAvailable = s.seatsAvailable.trim().toInt()
            )
            createVehicleUseCase(vehicle, photoBytes)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.emit(RegisterVehicleEvent.VehicleRegistered)
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, error = RegisterVehicleError.Unknown) }
                }
        }
    }

    private fun validate(s: RegisterVehicleUiState): RegisterVehicleError? {
        if (s.vehiclePhoto == null) return RegisterVehicleError.PhotoRequired
        if (s.brand.isBlank()) return RegisterVehicleError.BrandRequired
        if (s.model.isBlank()) return RegisterVehicleError.ModelRequired
        if (s.licensePlate.isBlank()) return RegisterVehicleError.LicensePlateRequired
        if (s.color.isBlank()) return RegisterVehicleError.ColorRequired
        if (s.year.isBlank()) return RegisterVehicleError.YearRequired
        val yearInt = s.year.trim().toIntOrNull()
        if (yearInt == null || yearInt < 1900 || yearInt > 2100) return RegisterVehicleError.YearInvalid
        if (s.seatsAvailable.isBlank()) return RegisterVehicleError.SeatsRequired
        val seats = s.seatsAvailable.trim().toIntOrNull()
        if (seats == null || seats < 1 || seats > 8) return RegisterVehicleError.SeatsInvalid
        return null
    }
}
