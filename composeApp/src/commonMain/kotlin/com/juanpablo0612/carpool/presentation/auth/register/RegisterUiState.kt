package com.juanpablo0612.carpool.presentation.auth.register

import com.juanpablo0612.carpool.domain.auth.model.AuthError
import com.juanpablo0612.carpool.domain.auth.util.ValidationError

data class RegisterUiState(
    val fullName: String = "",
    val fullNameError: ValidationError? = null,
    val email: String = "",
    val emailError: ValidationError? = null,
    val password: String = "",
    val passwordError: ValidationError? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: ValidationError? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isPassenger: Boolean = false,
    val isDriver: Boolean = false,
    val isLoading: Boolean = false,
    val error: AuthError? = null,
    val isSuccess: Boolean = false
)
