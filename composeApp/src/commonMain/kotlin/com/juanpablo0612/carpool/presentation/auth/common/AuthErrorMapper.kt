package com.juanpablo0612.carpool.presentation.auth.common

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_email_already_in_use
import enrutadoseia.composeapp.generated.resources.error_invalid_credentials
import enrutadoseia.composeapp.generated.resources.error_network
import enrutadoseia.composeapp.generated.resources.error_unknown
import enrutadoseia.composeapp.generated.resources.error_weak_password
import com.juanpablo0612.carpool.domain.auth.model.AuthError
import org.jetbrains.compose.resources.StringResource

fun AuthError.asStringResource(): StringResource {
    return when (this) {
        AuthError.InvalidCredentials -> Res.string.error_invalid_credentials
        AuthError.EmailAlreadyInUse -> Res.string.error_email_already_in_use
        AuthError.NetworkError -> Res.string.error_network
        AuthError.WeakPassword -> Res.string.error_weak_password
        AuthError.UnknownError -> Res.string.error_unknown
        AuthError.UserNotFound -> Res.string.error_invalid_credentials
    }
}
