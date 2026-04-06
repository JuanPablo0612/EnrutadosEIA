package com.juanpablo0612.carpool.presentation.auth.forgot_password

sealed interface ForgotPasswordAction {
    data class OnEmailChanged(val email: String) : ForgotPasswordAction
    data object OnSendResetLink : ForgotPasswordAction
    data object OnBackClick : ForgotPasswordAction
}
