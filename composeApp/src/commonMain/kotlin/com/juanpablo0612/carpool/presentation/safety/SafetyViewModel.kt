package com.juanpablo0612.carpool.presentation.safety

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.safety.model.SafetySettings
import com.juanpablo0612.carpool.domain.safety.use_case.AddEmergencyContactUseCase
import com.juanpablo0612.carpool.domain.safety.use_case.GetEmergencyContactsUseCase
import com.juanpablo0612.carpool.domain.safety.use_case.GetSafetySettingsUseCase
import com.juanpablo0612.carpool.domain.safety.use_case.RemoveEmergencyContactUseCase
import com.juanpablo0612.carpool.domain.safety.use_case.UpdateSafetySettingsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SafetyViewModel(
    private val getEmergencyContactsUseCase: GetEmergencyContactsUseCase,
    private val addEmergencyContactUseCase: AddEmergencyContactUseCase,
    private val removeEmergencyContactUseCase: RemoveEmergencyContactUseCase,
    private val getSafetySettingsUseCase: GetSafetySettingsUseCase,
    private val updateSafetySettingsUseCase: UpdateSafetySettingsUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SafetyUiState())
    val state: StateFlow<SafetyUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<SafetyEvent>()
    val events: SharedFlow<SafetyEvent> = _events.asSharedFlow()

    private val userId = authRepository.getCurrentUserId() ?: ""

    init {
        if (userId.isNotBlank()) loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val contactsResult = getEmergencyContactsUseCase(userId)
            val settingsResult = getSafetySettingsUseCase(userId)
            _state.update { state ->
                state.copy(
                    contacts = contactsResult.getOrDefault(emptyList()),
                    settings = settingsResult.getOrDefault(SafetySettings()),
                    isLoading = false
                )
            }
        }
    }

    fun onAction(action: SafetyAction) {
        when (action) {
            SafetyAction.OnAddContactClick ->
                _state.update { it.copy(showAddDialog = true, newContactName = "", newContactPhone = "", newContactNameError = null, newContactPhoneError = null) }
            SafetyAction.OnDismissAddDialog ->
                _state.update { it.copy(showAddDialog = false) }
            is SafetyAction.OnContactNameChange ->
                _state.update { it.copy(newContactName = action.name, newContactNameError = null) }
            is SafetyAction.OnContactPhoneChange ->
                _state.update { it.copy(newContactPhone = action.phone, newContactPhoneError = null) }
            SafetyAction.OnSaveContact -> saveContact()
            is SafetyAction.OnRemoveContact -> _state.update {
                it.copy(pendingRemoveContactId = action.contactId)
            }
            is SafetyAction.OnConfirmRemoveContact -> {
                _state.update { it.copy(pendingRemoveContactId = null) }
                removeContact(action.contactId)
            }
            SafetyAction.OnDismissRemoveContact -> _state.update { it.copy(pendingRemoveContactId = null) }
            is SafetyAction.OnToggleAutoShare -> updateSettings(autoShare = action.enabled)
            is SafetyAction.OnToggleVibrateSos -> updateSettings(vibrateSos = action.enabled)
            SafetyAction.OnBackClick -> viewModelScope.launch { _events.emit(SafetyEvent.NavigateBack) }
        }
    }

    private fun saveContact() {
        val state = _state.value
        val name = state.newContactName.trim()
        val phone = state.newContactPhone.trim()
        var hasError = false
        if (name.isBlank()) {
            _state.update { it.copy(newContactNameError = SafetyContactFieldError.NameEmpty) }
            hasError = true
        }
        if (phone.isBlank()) {
            _state.update { it.copy(newContactPhoneError = SafetyContactFieldError.PhoneEmpty) }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            addEmergencyContactUseCase(userId, name, phone, state.contacts.size)
                .onSuccess {
                    val updated = getEmergencyContactsUseCase(userId).getOrDefault(state.contacts)
                    _state.update { it.copy(contacts = updated, showAddDialog = false, isSaving = false) }
                }
                .onFailure {
                    _state.update { it.copy(isSaving = false) }
                }
        }
    }

    private fun removeContact(contactId: String) {
        viewModelScope.launch {
            removeEmergencyContactUseCase(userId, contactId)
                .onSuccess {
                    _state.update { it.copy(contacts = it.contacts.filter { c -> c.id != contactId }) }
                }
        }
    }

    private fun updateSettings(
        autoShare: Boolean = _state.value.settings.autoShareTrip,
        vibrateSos: Boolean = _state.value.settings.vibrateSos
    ) {
        val newSettings = SafetySettings(autoShareTrip = autoShare, vibrateSos = vibrateSos)
        _state.update { it.copy(settings = newSettings) }
        viewModelScope.launch {
            updateSafetySettingsUseCase(userId, newSettings)
        }
    }
}
