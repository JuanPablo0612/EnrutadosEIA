package com.juanpablo0612.carpool.presentation.booking

import com.juanpablo0612.carpool.core.exception.AppException
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

fun Throwable.toBookingError(): BookingError = when (this) {
    is AppException.BookingException.NotAuthenticated -> BookingError.NotAuthenticated
    is AppException.BookingException.NoSeatsAvailable -> BookingError.NoSeatsAvailable
    is AppException.BookingException.AlreadyBooked -> BookingError.AlreadyBooked
    is AppException.BookingException.VehicleNotFound -> BookingError.VehicleNotFound
    else -> BookingError.Unknown
}

fun BookingError.asStringResource(): StringResource = when (this) {
    BookingError.NotAuthenticated -> Res.string.error_user_not_authenticated
    BookingError.NoSeatsAvailable -> Res.string.error_no_seats_available
    BookingError.AlreadyBooked -> Res.string.error_already_booked
    BookingError.VehicleNotFound -> Res.string.error_booking_vehicle_not_found
    BookingError.Unknown -> Res.string.error_unknown
}
