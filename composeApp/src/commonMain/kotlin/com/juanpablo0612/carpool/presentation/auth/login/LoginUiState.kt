package com.juanpablo0612.carpool.presentation.auth.login

import com.juanpablo0612.carpool.domain.auth.model.AuthError
import com.juanpablo0612.carpool.domain.auth.util.ValidationError

data class LoginUiState(
    val email: String = "",
    val emailError: ValidationError? = null,
    val password: String = "",
    val passwordError: ValidationError? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: AuthError? = null
)
