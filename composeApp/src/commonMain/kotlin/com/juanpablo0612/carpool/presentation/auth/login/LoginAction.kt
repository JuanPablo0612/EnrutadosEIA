package com.juanpablo0612.carpool.presentation.auth.login

sealed class LoginAction {
    data class OnEmailChanged(val email: String) : LoginAction()
    data class OnPasswordChanged(val password: String) : LoginAction()
    data object OnTogglePasswordVisibility : LoginAction()
    data object OnLoginClicked : LoginAction()
}
