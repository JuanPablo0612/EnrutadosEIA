package com.juanpablo0612.carpool.presentation.vehicles.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicles.use_case.CreateVehicleUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.GetVehicleByIdUseCase
import com.juanpablo0612.carpool.domain.vehicles.use_case.UpdateVehicleUseCase
import com.juanpablo0612.carpool.presentation.vehicles.register.RegisterVehicleUiState.Companion.PLATE_REGEX
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
    private val vehicleId: String?,
    private val createVehicleUseCase: CreateVehicleUseCase,
    private val updateVehicleUseCase: UpdateVehicleUseCase,
    private val getVehicleByIdUseCase: GetVehicleByIdUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterVehicleUiState())
    val state: StateFlow<RegisterVehicleUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<RegisterVehicleEvent>()
    val events: SharedFlow<RegisterVehicleEvent> = _events.asSharedFlow()

    init {
        if (vehicleId != null) {
            loadVehicle(vehicleId)
        }
    }

    private fun loadVehicle(id: String) {
        viewModelScope.launch {
            getVehicleByIdUseCase(id).onSuccess { vehicle ->
                val isCustomColor = vehicle.color !in RegisterVehicleUiState.PRESET_COLORS
                val isCustomBrand = vehicle.brand !in RegisterVehicleUiState.COMMON_BRANDS
                _state.update { s ->
                    s.copy(
                        mode = RegisterVehicleUiState.Mode.Edit,
                        vehicleId = vehicle.id,
                        existingPhotoUrl = vehicle.photoUrl.ifBlank { null },
                        brand = vehicle.brand,
                        isCustomBrand = isCustomBrand,
                        model = vehicle.model,
                        plate = vehicle.licensePlate.filter { it.isLetterOrDigit() }.uppercase(),
                        color = if (isCustomColor) "Otro" else vehicle.color,
                        isCustomColor = isCustomColor,
                        customColor = if (isCustomColor) vehicle.color else "",
                        year = vehicle.year,
                        seatCount = vehicle.seatsAvailable,
                        type = vehicle.type,
                        soatDate = vehicle.soatExpiresOn,
                        tecnomecanicaDate = vehicle.tecnomecanicaExpiresOn,
                        showDocuments = vehicle.soatExpiresOn != null || vehicle.tecnomecanicaExpiresOn != null,
                    )
                }
            }
        }
    }

    fun onAction(action: RegisterVehicleAction) {
        when (action) {
            is RegisterVehicleAction.OnPhotoSelected ->
                _state.update { it.copy(photoFile = action.photo, showPhotoSheet = false) }

            is RegisterVehicleAction.OnBrandSelected ->
                _state.update {
                    it.copy(
                        brand = action.brand,
                        isCustomBrand = action.brand == "Otro",
                        showBrandDropdown = false,
                        brandError = false,
                    )
                }

            RegisterVehicleAction.OnToggleBrandDropdown ->
                _state.update { it.copy(showBrandDropdown = !it.showBrandDropdown) }

            RegisterVehicleAction.OnToggleCustomBrand ->
                _state.update { it.copy(isCustomBrand = !it.isCustomBrand, brand = "", brandError = false) }

            is RegisterVehicleAction.OnModelChanged ->
                _state.update { it.copy(model = action.model, modelError = false) }

            is RegisterVehicleAction.OnPlateChanged -> {
                val raw = action.plate.uppercase().filter { it.isLetterOrDigit() }.take(6)
                _state.update { it.copy(plate = raw, plateError = false) }
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
                _state.update { it.copy(year = action.year, showYearDropdown = false) }

            RegisterVehicleAction.OnToggleYearDropdown ->
                _state.update { it.copy(showYearDropdown = !it.showYearDropdown) }

            is RegisterVehicleAction.OnSeatCountChanged ->
                _state.update { it.copy(seatCount = action.count) }

            is RegisterVehicleAction.OnTypeSelected ->
                _state.update { it.copy(type = if (it.type == action.type) null else action.type) }

            is RegisterVehicleAction.OnSoatDateSelected ->
                _state.update { it.copy(soatDate = action.date, showSoatDatePicker = false) }

            is RegisterVehicleAction.OnTecnoDateSelected ->
                _state.update { it.copy(tecnomecanicaDate = action.date, showTecnoDatePicker = false) }

            RegisterVehicleAction.OnToggleDocuments ->
                _state.update { it.copy(showDocuments = !it.showDocuments) }

            RegisterVehicleAction.OnShowPhotoSheet ->
                _state.update { it.copy(showPhotoSheet = true) }

            RegisterVehicleAction.OnDismissPhotoSheet ->
                _state.update { it.copy(showPhotoSheet = false) }

            RegisterVehicleAction.OnShowSoatDatePicker ->
                _state.update { it.copy(showSoatDatePicker = true) }

            RegisterVehicleAction.OnDismissSoatDatePicker ->
                _state.update { it.copy(showSoatDatePicker = false) }

            RegisterVehicleAction.OnShowTecnoDatePicker ->
                _state.update { it.copy(showTecnoDatePicker = true) }

            RegisterVehicleAction.OnDismissTecnoDatePicker ->
                _state.update { it.copy(showTecnoDatePicker = false) }

            RegisterVehicleAction.OnSaveClick -> saveVehicle()

            RegisterVehicleAction.OnBackClick -> viewModelScope.launch {
                _events.emit(RegisterVehicleEvent.NavigateBack)
            }
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
        if (s.effectiveColor.isBlank()) {
            _state.update { it.copy(colorError = true) }
            hasError = true
        }
        if (hasError) return

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
                year = s.year,
                seatsAvailable = s.seatCount,
                photoUrl = s.existingPhotoUrl ?: "",
                type = s.type,
                soatExpiresOn = s.soatDate,
                tecnomecanicaExpiresOn = s.tecnomecanicaDate,
            )

            val photoBytes = s.photoFile?.readBytes()

            val result = if (s.mode == RegisterVehicleUiState.Mode.Create) {
                if (photoBytes == null) {
                    _state.update { it.copy(isSaving = false, generalError = RegisterVehicleError.PhotoRequired) }
                    return@launch
                }
                createVehicleUseCase(vehicle, photoBytes)
            } else {
                updateVehicleUseCase(vehicle, photoBytes)
            }

            result
                .onSuccess {
                    _state.update { it.copy(isSaving = false) }
                    _events.emit(RegisterVehicleEvent.VehicleRegistered)
                }
                .onFailure {
                    _state.update { it.copy(isSaving = false, generalError = RegisterVehicleError.Unknown) }
                }
        }
    }
}
