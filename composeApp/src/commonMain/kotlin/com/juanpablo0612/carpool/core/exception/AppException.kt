package com.juanpablo0612.carpool.core.exception

/**
 * Base class for all application-specific exceptions.
 */
sealed class AppException : Exception() {
    sealed class AuthException : AppException() {
        data object InvalidCredentials : AuthException()
        data object UserNotFound : AuthException()
        data object EmailAlreadyInUse : AuthException()
        data object WeakPassword : AuthException()
        data object NetworkError : AuthException()
        data object Unknown : AuthException()
    }
}
