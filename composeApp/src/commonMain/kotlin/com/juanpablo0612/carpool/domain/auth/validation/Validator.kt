package com.juanpablo0612.carpool.domain.auth.validation

object Validator {
    // Basic shape check (local@domain.tld) — deliberately not exhaustive RFC 5322, just enough to
    // reject obviously malformed input before the EIA-domain-specific check runs.
    private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error(ValidationError.EmailEmpty)
            !EMAIL_REGEX.matches(email) -> ValidationResult.Error(ValidationError.EmailInvalid)
            !email.endsWith("@eia.edu.co", ignoreCase = true) -> ValidationResult.Error(ValidationError.EmailNotEia)
            else -> ValidationResult.Success
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error(ValidationError.PasswordEmpty)
            password.length < 8 -> ValidationResult.Error(ValidationError.PasswordTooShort)
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

    fun validatePhone(phone: String): ValidationResult {
        val digits = phone.filter { it.isDigit() }
        return when {
            phone.isBlank() -> ValidationResult.Error(ValidationError.PhoneEmpty)
            digits.length != 10 || !digits.startsWith("3") -> ValidationResult.Error(ValidationError.PhoneInvalid)
            else -> ValidationResult.Success
        }
    }
}
