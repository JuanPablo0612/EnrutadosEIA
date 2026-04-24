package com.juanpablo0612.carpool.presentation.bookings

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.booking.model.BookingError
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

fun Throwable.toBookingError(): BookingError = when (this) {
    is AppException.BookingException.NotAuthenticated -> BookingError.NotAuthenticated
    is AppException.BookingException.NoSeatsAvailable -> BookingError.NoSeatsAvailable
    is AppException.BookingException.AlreadyBooked -> BookingError.AlreadyBooked
    is AppException.BookingException.BookingNotFound -> BookingError.BookingNotFound
    is AppException.BookingException.Unauthorized -> BookingError.Unauthorized
    else -> BookingError.Unknown
}

fun BookingError.asStringResource(): StringResource = when (this) {
    BookingError.NotAuthenticated -> Res.string.error_user_not_authenticated
    BookingError.NoSeatsAvailable -> Res.string.error_no_seats_available
    BookingError.AlreadyBooked -> Res.string.error_already_booked
    BookingError.BookingNotFound -> Res.string.error_booking_not_found
    BookingError.Unauthorized -> Res.string.error_unknown
    BookingError.Unknown -> Res.string.error_unknown
}
