package com.juanpablo0612.carpool.presentation.auth.forgot_password

import com.juanpablo0612.carpool.domain.auth.model.AuthError
import com.juanpablo0612.carpool.domain.auth.util.ValidationError

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: ValidationError? = null,
    val isSuccess: Boolean = false,
    val error: AuthError? = null
)
