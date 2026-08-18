package com.juanpablo0612.carpool.domain.auth.validation

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val error: ValidationError) : ValidationResult()
}

sealed class ValidationError {
    data object EmailEmpty : ValidationError()
    data object EmailInvalid : ValidationError()
    data object EmailNotEia : ValidationError()
    data object PasswordEmpty : ValidationError()
    data object PasswordTooShort : ValidationError()
    data object NameEmpty : ValidationError()
    data object NameTooShort : ValidationError()
    data object ConfirmPasswordEmpty : ValidationError()
    data object PasswordsDoNotMatch : ValidationError()
    data object RoleNotSelected : ValidationError()
    data object PhoneEmpty : ValidationError()
    data object PhoneInvalid : ValidationError()
    data object TermsNotAccepted : ValidationError()
}
