package com.juanpablo0612.carpool.domain.error

sealed class AuthError {
    object InvalidCredentials : AuthError()
    object UserNotFound : AuthError()
    object EmailAlreadyInUse : AuthError()
    object WeakPassword : AuthError()
    object NetworkError : AuthError()
    object UnknownError : AuthError()
}
