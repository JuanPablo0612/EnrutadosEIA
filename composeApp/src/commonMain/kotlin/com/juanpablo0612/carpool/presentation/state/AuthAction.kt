package com.juanpablo0612.carpool.presentation.state

sealed class AuthAction {
    data class Login(val email: String, val password: String) : AuthAction()
    data class Register(val email: String, val password: String) : AuthAction()
    object Logout : AuthAction()
    object ClearError : AuthAction()
}

sealed class AuthEvent {
    object NavigateToHome : AuthEvent()
    object NavigateToLogin : AuthEvent()
    data class ShowErrorMessage(val message: String) : AuthEvent()
}
