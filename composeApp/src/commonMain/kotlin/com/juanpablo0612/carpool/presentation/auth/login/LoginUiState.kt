package com.juanpablo0612.carpool.presentation.auth.login

import com.juanpablo0612.carpool.domain.auth.model.AuthError

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: AuthError? = null,
    val isSuccess: Boolean = false
)
