package com.juanpablo0612.carpool.presentation.safety

import com.juanpablo0612.carpool.domain.safety.model.EmergencyContact
import com.juanpablo0612.carpool.domain.safety.model.SafetySettings

data class SafetyUiState(
    val contacts: List<EmergencyContact> = emptyList(),
    val settings: SafetySettings = SafetySettings(),
    val showAddDialog: Boolean = false,
    val newContactName: String = "",
    val newContactPhone: String = "",
    val newContactNameError: SafetyContactFieldError? = null,
    val newContactPhoneError: SafetyContactFieldError? = null,
    val pendingRemoveContactId: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: SafetyError? = null,
    val saveError: SafetyError? = null,
    val actionError: Boolean = false
) {
    val canAddContact: Boolean get() = contacts.size < MAX_CONTACTS

    companion object {
        const val MAX_CONTACTS = 2
    }
}
