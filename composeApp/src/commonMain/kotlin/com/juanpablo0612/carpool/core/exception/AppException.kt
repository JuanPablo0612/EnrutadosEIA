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

    sealed class BookingException : AppException() {
        data object NotAuthenticated : BookingException()
        data object NoSeatsAvailable : BookingException()
        data object AlreadyBooked : BookingException()
        data object VehicleNotFound : BookingException()
        data object Unknown : BookingException()
    }

    sealed class TripException : AppException() {
        data object Unknown : TripException()
    }

    sealed class RouteException : AppException() {
        data object Unknown : RouteException()
    }

    sealed class VehicleException : AppException() {
        data object Unknown : VehicleException()
    }

    sealed class PlaceException : AppException() {
        data object NotAuthenticated : PlaceException()
        data object Unauthorized : PlaceException()
        data object Unknown : PlaceException()
    }

    sealed class ChatException : AppException() {
        data object Unknown : ChatException()
    }

    sealed class RatingException : AppException() {
        data object Unknown : RatingException()
    }

    sealed class NotificationException : AppException() {
        data object Unknown : NotificationException()
    }

    sealed class SafetyException : AppException() {
        data object MaxContactsReached : SafetyException()
        data object Unknown : SafetyException()
    }
}
