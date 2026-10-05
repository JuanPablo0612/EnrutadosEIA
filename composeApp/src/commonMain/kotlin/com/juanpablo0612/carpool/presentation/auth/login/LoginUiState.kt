package com.juanpablo0612.carpool.presentation.auth.login

import com.juanpablo0612.carpool.domain.auth.validation.EiaEmail
import com.juanpablo0612.carpool.domain.auth.validation.ValidationError
import com.juanpablo0612.carpool.presentation.auth.AuthError

data class LoginUiState(
    val email: String = "",
    val emailError: ValidationError? = null,
    val password: String = "",
    val passwordError: ValidationError? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: AuthError? = null
) {
    /**
     * Firebase answers a wrong password and an address with no account the same way, so the
     * screen can't tell which happened: it marks both fields and lets the user say which it was.
     */
    val isCredentialsRejected: Boolean
        get() = error is AuthError.InvalidCredentials || error is AuthError.UserNotFound

    /** The full address typed so far, to prefill the sign-up or password-reset form; null if empty. */
    val typedEmail: String?
        get() = EiaEmail.fromInput(email).ifEmpty { null }
}
