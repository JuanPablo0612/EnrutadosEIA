package com.juanpablo0612.carpool.presentation.vehicle.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import com.juanpablo0612.carpool.presentation.vehicle.register.RegisterVehicleUiState.Companion.PLATE_REGEX
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SAVED_BANNER_DURATION_MS = 900L

class RegisterVehicleViewModel(
    private val vehicleId: String?,
    private val vehicleRepository: VehicleRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterVehicleUiState())
    val state: StateFlow<RegisterVehicleUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<RegisterVehicleEvent>()
    val events: SharedFlow<RegisterVehicleEvent> = _events.asSharedFlow()

    init {
        if (vehicleId != null) {
            loadVehicle(vehicleId)
        } else {
            _state.update { it.copy(initialSnapshot = it.snapshot) }
        }
    }

    private fun loadVehicle(id: String) {
        viewModelScope.launch {
            vehicleRepository.getVehicleById(id).onSuccess { vehicle ->
                val isCustomColor = vehicle.color !in RegisterVehicleUiState.PRESET_COLORS
                _state.update { s ->
                    s.copy(
                        mode = RegisterVehicleUiState.Mode.Edit,
                        vehicleId = vehicle.id,
                        existingPhotoUrl = vehicle.photoUrl.ifBlank { null },
                        brand = vehicle.brand,
                        model = vehicle.model,
                        plate = vehicle.licensePlate.filter { it.isLetterOrDigit() }.uppercase(),
                        color = if (isCustomColor) "Otro" else vehicle.color,
                        isCustomColor = isCustomColor,
                        customColor = if (isCustomColor) vehicle.color else "",
                        year = vehicle.year,
                        seatCount = vehicle.seatsAvailable,
                        type = vehicle.type,
                        isPrimary = vehicle.isPrimary,
                    )
                }
                _state.update { it.copy(initialSnapshot = it.snapshot) }
            }
        }
    }

    fun onAction(action: RegisterVehicleAction) {
        when (action) {
            is RegisterVehicleAction.OnPhotoSelected ->
                _state.update { it.copy(photoFile = action.photo, showPhotoSheet = false) }

            is RegisterVehicleAction.OnBrandChanged ->
                _state.update { it.copy(brand = action.brand, brandError = false) }

            is RegisterVehicleAction.OnModelChanged ->
                _state.update { it.copy(model = action.model, modelError = false) }

            is RegisterVehicleAction.OnPlateChanged -> {
                val raw = action.plate.uppercase().filter { it.isLetterOrDigit() }.take(6)
                // Only judge once the plate is at its full length — flagging an error mid-type
                // (e.g. after 2 characters) would be premature and annoying.
                val error = raw.length == 6 && !PLATE_REGEX.matches(raw)
                _state.update { it.copy(plate = raw, plateError = error) }
            }

            is RegisterVehicleAction.OnColorSelected ->
                _state.update {
                    it.copy(
                        color = action.color,
                        isCustomColor = action.color == "Otro",
                        customColor = if (action.color != "Otro") "" else it.customColor,
                        colorError = false,
                    )
                }

            is RegisterVehicleAction.OnCustomColorChanged ->
                _state.update { it.copy(customColor = action.color, colorError = false) }

            is RegisterVehicleAction.OnYearSelected ->
                _state.update { it.copy(year = action.year, showYearDropdown = false, yearError = false) }

            RegisterVehicleAction.OnToggleYearDropdown ->
                _state.update { it.copy(showYearDropdown = !it.showYearDropdown) }

            is RegisterVehicleAction.OnSeatCountChanged ->
                _state.update { it.copy(seatCount = action.count) }

            is RegisterVehicleAction.OnTypeSelected ->
                _state.update { it.copy(type = if (it.type == action.type) null else action.type) }

            RegisterVehicleAction.OnShowPhotoSheet ->
                _state.update { it.copy(showPhotoSheet = true) }

            RegisterVehicleAction.OnDismissPhotoSheet ->
                _state.update { it.copy(showPhotoSheet = false) }

            RegisterVehicleAction.OnRemovePhoto ->
                _state.update { it.copy(photoFile = null, existingPhotoUrl = null, showPhotoSheet = false) }

            RegisterVehicleAction.OnSaveClick -> saveVehicle()

            RegisterVehicleAction.OnBackClick -> {
                if (_state.value.isDirty) {
                    _state.update { it.copy(showDiscardConfirm = true) }
                } else {
                    viewModelScope.launch { _events.emit(RegisterVehicleEvent.NavigateBack) }
                }
            }

            RegisterVehicleAction.OnConfirmDiscard -> {
                _state.update { it.copy(showDiscardConfirm = false) }
                viewModelScope.launch { _events.emit(RegisterVehicleEvent.NavigateBack) }
            }

            RegisterVehicleAction.OnDismissDiscardConfirm ->
                _state.update { it.copy(showDiscardConfirm = false) }
        }
    }

    private fun saveVehicle() {
        val s = _state.value
        var hasError = false

        if (s.brand.isBlank()) {
            _state.update { it.copy(brandError = true) }
            hasError = true
        }
        if (s.model.isBlank()) {
            _state.update { it.copy(modelError = true) }
            hasError = true
        }
        if (!PLATE_REGEX.matches(s.plate)) {
            _state.update { it.copy(plateError = true) }
            hasError = true
        }
        if (s.year == null) {
            _state.update { it.copy(yearError = true) }
            hasError = true
        }
        if (s.effectiveColor.isBlank()) {
            _state.update { it.copy(colorError = true) }
            hasError = true
        }
        if (hasError) return
        val year = s.year ?: return

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            _state.update { it.copy(generalError = RegisterVehicleError.UserNotAuthenticated) }
            return
        }

        _state.update { it.copy(isSaving = true, generalError = null) }

        viewModelScope.launch {
            val vehicle = Vehicle(
                id = s.vehicleId,
                driverId = userId,
                brand = s.brand.trim(),
                model = s.model.trim(),
                licensePlate = s.plate,
                color = s.effectiveColor.trim(),
                year = year,
                seatsAvailable = s.seatCount,
                photoUrl = s.existingPhotoUrl ?: "",
                isPrimary = s.isPrimary,
                type = s.type,
            )

            val photoBytes = s.photoFile?.readBytes()

            val result = if (s.mode == RegisterVehicleUiState.Mode.Create) {
                vehicleRepository.createVehicle(vehicle, photoBytes)
            } else {
                vehicleRepository.updateVehicle(vehicle, photoBytes)
            }

            result
                .onSuccess {
                    _state.update { it.copy(isSaving = false, isSaved = true) }
                    delay(SAVED_BANNER_DURATION_MS)
                    _events.emit(RegisterVehicleEvent.VehicleRegistered)
                }
                .onFailure {
                    _state.update { it.copy(isSaving = false, generalError = RegisterVehicleError.Unknown) }
                }
        }
    }
}
