package com.juanpablo0612.carpool.presentation.bookings.passenger

sealed class PassengerBookingsAction {
    data object OnBackClick : PassengerBookingsAction()
    data class OnCancelBookingClick(val bookingId: String) : PassengerBookingsAction()
}
