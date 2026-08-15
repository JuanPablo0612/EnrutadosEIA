package com.juanpablo0612.carpool.presentation.safety

sealed class SafetyAction {
    data object OnAddContactClick : SafetyAction()
    data object OnDismissAddDialog : SafetyAction()
    data class OnContactNameChange(val name: String) : SafetyAction()
    data class OnContactPhoneChange(val phone: String) : SafetyAction()
    data object OnSaveContact : SafetyAction()
    data class OnRemoveContact(val contactId: String) : SafetyAction()
    data class OnConfirmRemoveContact(val contactId: String) : SafetyAction()
    data object OnDismissRemoveContact : SafetyAction()
    data class OnToggleAutoShare(val enabled: Boolean) : SafetyAction()
    data class OnToggleVibrateSos(val enabled: Boolean) : SafetyAction()
    data object OnBackClick : SafetyAction()
}
