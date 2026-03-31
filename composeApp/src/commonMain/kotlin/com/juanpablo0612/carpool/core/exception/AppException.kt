package com.juanpablo0612.carpool.core.exception

/**
 * Base class for all application-specific exceptions.
 */
sealed class AppException : Exception() {
    sealed class AuthException : AppException() {
        object InvalidCredentials : AuthException()
        object UserNotFound : AuthException()
        object EmailAlreadyInUse : AuthException()
        object WeakPassword : AuthException()
        object NetworkError : AuthException()
        object Unknown : AuthException()
    }
}
