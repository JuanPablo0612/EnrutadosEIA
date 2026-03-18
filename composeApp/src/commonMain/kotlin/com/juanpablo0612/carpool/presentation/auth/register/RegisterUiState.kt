package com.juanpablo0612.carpool.presentation.auth.register

import com.juanpablo0612.carpool.domain.auth.model.AuthError

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: AuthError? = null,
    val isSuccess: Boolean = false
)
