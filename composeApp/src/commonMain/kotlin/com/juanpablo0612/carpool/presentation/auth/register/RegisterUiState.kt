package com.juanpablo0612.carpool.presentation.auth.register

import com.juanpablo0612.carpool.domain.auth.validation.EiaEmail
import com.juanpablo0612.carpool.domain.auth.validation.PasswordStrength
import com.juanpablo0612.carpool.domain.auth.validation.ValidationError
import com.juanpablo0612.carpool.presentation.auth.AuthError
import io.github.vinceglb.filekit.PlatformFile

data class RegisterUiState(
    val currentStep: Int = 1,
    val fullName: String = "",
    val fullNameError: ValidationError? = null,
    /** What the user typed: usually just the username, completed with [EiaEmail] on submit. */
    val email: String = "",
    val emailError: ValidationError? = null,
    val password: String = "",
    val passwordError: ValidationError? = null,
    /** Null while the password is empty, so no meter shows before the user types. */
    val passwordStrength: PasswordStrength? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: ValidationError? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val photoFile: PlatformFile? = null,
    val photoError: Boolean = false,
    val phone: String = "",
    val phoneError: ValidationError? = null,
    val hasAcceptedTerms: Boolean = false,
    val termsError: Boolean = false,
    val isLoading: Boolean = false,
    val error: AuthError? = null
)
