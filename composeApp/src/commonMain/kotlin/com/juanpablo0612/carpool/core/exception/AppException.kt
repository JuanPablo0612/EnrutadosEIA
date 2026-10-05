package com.juanpablo0612.carpool.core.exception

import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError

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

        /** The trip has left, finished or been cancelled, so its seats can no longer change. */
        data object TripClosed : BookingException()
        data object Unknown : BookingException()
    }

    sealed class TripException : AppException() {
        data object NotAuthenticated : TripException()
        data object VehicleNotFound : TripException()
        /** The trip failed validation; [errors] in the order they were found. */
        data class Invalid(val errors: List<TripValidationError>) : TripException()
        /** The frequent route was saved as [routeId], but publishing the trip then failed. */
        data class RouteSavedTripFailed(val routeId: String) : TripException()
        /** The trip is not the caller's, has left or is no longer active, so it can't be edited. */
        data object NotEditable : TripException()
        /** Fewer seats than the [confirmedSeats] already given to passengers. */
        data class SeatsBelowConfirmed(val confirmedSeats: Int) : TripException()
        /** Removing these stops would strand passengers whose open bookings meet the trip there. */
        data class StopInUse(val stopNames: List<String>) : TripException()
        data object Unknown : TripException()
    }

    sealed class RouteException : AppException() {
        data object NotAuthenticated : RouteException()
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
        data object AlreadyRated : RatingException()
        data object Unknown : RatingException()
    }

    sealed class NotificationException : AppException() {
        data object Unknown : NotificationException()
    }
}
