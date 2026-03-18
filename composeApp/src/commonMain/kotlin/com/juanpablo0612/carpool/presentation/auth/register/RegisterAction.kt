package com.juanpablo0612.carpool.presentation.auth.register

sealed class RegisterAction {
    data class OnFullNameChanged(val fullName: String) : RegisterAction()
    data class OnEmailChanged(val email: String) : RegisterAction()
    data class OnPasswordChanged(val password: String) : RegisterAction()
    data class OnConfirmPasswordChanged(val confirmPassword: String) : RegisterAction()
    data class OnPassengerChanged(val isPassenger: Boolean) : RegisterAction()
    data class OnDriverChanged(val isDriver: Boolean) : RegisterAction()
    object OnRegisterClicked : RegisterAction()
    object OnClearError : RegisterAction()
}
