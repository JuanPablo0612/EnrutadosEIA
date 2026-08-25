package com.juanpablo0612.carpool.presentation.auth.forgotpassword

import com.juanpablo0612.carpool.presentation.auth.AuthError
import com.juanpablo0612.carpool.domain.auth.validation.ValidationError

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: ValidationError? = null,
    val isSuccess: Boolean = false,
    val obfuscatedEmail: String = "",
    val resendCountdown: Int = 0,
    val error: AuthError? = null
)
