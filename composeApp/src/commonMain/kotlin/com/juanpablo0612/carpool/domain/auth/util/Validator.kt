package com.juanpablo0612.carpool.domain.auth.util

object Validator {
    private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error(ValidationError.EmailEmpty)
            !email.matches(Regex(EMAIL_REGEX)) -> ValidationResult.Error(ValidationError.EmailInvalid)
            else -> ValidationResult.Success
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error(ValidationError.PasswordEmpty)
            password.length < 6 -> ValidationResult.Error(ValidationError.PasswordTooShort)
            else -> ValidationResult.Success
        }
    }

    fun validateFullName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error(ValidationError.NameEmpty)
            name.trim().split(" ").size < 2 -> ValidationResult.Error(ValidationError.NameTooShort)
            else -> ValidationResult.Success
        }
    }

    fun validateConfirmPassword(password: String, confirm: String): ValidationResult {
        return when {
            confirm.isBlank() -> ValidationResult.Error(ValidationError.ConfirmPasswordEmpty)
            password != confirm -> ValidationResult.Error(ValidationError.PasswordsDoNotMatch)
            else -> ValidationResult.Success
        }
    }

    fun validateRole(isPassenger: Boolean, isDriver: Boolean): ValidationResult {
        return if (!isPassenger && !isDriver) {
            ValidationResult.Error(ValidationError.RoleNotSelected)
        } else {
            ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val error: ValidationError) : ValidationResult()
}

sealed class ValidationError {
    data object EmailEmpty : ValidationError()
    data object EmailInvalid : ValidationError()
    data object PasswordEmpty : ValidationError()
    data object PasswordTooShort : ValidationError()
    data object NameEmpty : ValidationError()
    data object NameTooShort : ValidationError()
    data object ConfirmPasswordEmpty : ValidationError()
    data object PasswordsDoNotMatch : ValidationError()
    data object RoleNotSelected : ValidationError()
}
