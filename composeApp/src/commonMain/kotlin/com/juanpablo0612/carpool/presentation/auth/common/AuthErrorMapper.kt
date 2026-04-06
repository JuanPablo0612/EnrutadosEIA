package com.juanpablo0612.carpool.presentation.auth.common

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.auth.model.AuthError
import com.juanpablo0612.carpool.domain.auth.util.ValidationError
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

/**
 * Maps domain-agnostic exceptions to UI-specific error models.
 * This lives in the presentation layer because AuthError is a UI class.
 */
fun Throwable.toAuthError(): AuthError {
    return when (this) {
        is AppException.AuthException.InvalidCredentials -> AuthError.InvalidCredentials
        is AppException.AuthException.EmailAlreadyInUse -> AuthError.EmailAlreadyInUse
        is AppException.AuthException.WeakPassword -> AuthError.WeakPassword
        is AppException.AuthException.NetworkError -> AuthError.NetworkError
        is AppException.AuthException.UserNotFound -> AuthError.UserNotFound
        else -> AuthError.UnknownError
    }
}

fun AuthError.asStringResource(): StringResource {
    return when (this) {
        AuthError.InvalidCredentials -> Res.string.error_invalid_credentials
        AuthError.EmailAlreadyInUse -> Res.string.error_email_already_in_use
        AuthError.NetworkError -> Res.string.error_network
        AuthError.WeakPassword -> Res.string.error_weak_password
        AuthError.UnknownError -> Res.string.error_unknown
        AuthError.UserNotFound -> Res.string.error_user_not_found
    }
}

fun ValidationError.asStringResource(): StringResource {
    return when (this) {
        ValidationError.EmailEmpty -> Res.string.error_email_empty
        ValidationError.EmailInvalid -> Res.string.error_email_invalid
        ValidationError.PasswordEmpty -> Res.string.error_password_empty
        ValidationError.PasswordTooShort -> Res.string.error_password_too_short
        ValidationError.NameEmpty -> Res.string.error_name_empty
        ValidationError.NameTooShort -> Res.string.error_name_too_short
        ValidationError.ConfirmPasswordEmpty -> Res.string.error_confirm_password_empty
        ValidationError.PasswordsDoNotMatch -> Res.string.error_passwords_do_not_match
    }
}
