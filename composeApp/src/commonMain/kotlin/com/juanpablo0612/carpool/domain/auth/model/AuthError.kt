package com.juanpablo0612.carpool.domain.auth.model

sealed class AuthError {
    data object InvalidCredentials : AuthError()
    data object UserNotFound : AuthError()
    data object EmailAlreadyInUse : AuthError()
    data object WeakPassword : AuthError()
    data object NetworkError : AuthError()
    data object UnknownError : AuthError()
}
