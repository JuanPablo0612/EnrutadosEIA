package com.juanpablo0612.carpool.presentation.auth.register

sealed class RegisterAction {
    data class OnEmailChanged(val email: String) : RegisterAction()
    data class OnPasswordChanged(val password: String) : RegisterAction()
    object OnRegisterClicked : RegisterAction()
    object OnClearError : RegisterAction()
}
