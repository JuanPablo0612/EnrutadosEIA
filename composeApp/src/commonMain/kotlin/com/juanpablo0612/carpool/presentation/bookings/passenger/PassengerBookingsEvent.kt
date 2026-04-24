package com.juanpablo0612.carpool.presentation.bookings.passenger

sealed class PassengerBookingsEvent {
    data object NavigateBack : PassengerBookingsEvent()
}
