package com.juanpablo0612.carpool.presentation.auth.common

sealed class AuthEvent {
    object NavigateToHome : AuthEvent()
    object NavigateToLogin : AuthEvent()
    data class ShowErrorMessage(val message: String) : AuthEvent()
}
