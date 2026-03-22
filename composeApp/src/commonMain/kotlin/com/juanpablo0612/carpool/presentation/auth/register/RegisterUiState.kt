package com.juanpablo0612.carpool.presentation.auth.register

import com.juanpablo0612.carpool.domain.auth.model.AuthError

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isPassenger: Boolean = false,
    val isDriver: Boolean = false,
    val isLoading: Boolean = false,
    val error: AuthError? = null,
    val isSuccess: Boolean = false
)
