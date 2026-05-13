package com.juanpablo0612.carpool.presentation.bookings.passenger

sealed class PassengerBookingsAction {
    data object OnBackClick : PassengerBookingsAction()
    data class OnTabSelected(val tab: BookingTab) : PassengerBookingsAction()
    data class OnCancelBookingClick(val bookingId: String) : PassengerBookingsAction()
    data class OnConfirmCancel(val bookingId: String) : PassengerBookingsAction()
    data object OnDismissCancelDialog : PassengerBookingsAction()
    data object OnDismissError : PassengerBookingsAction()
    data object OnDismissSuccess : PassengerBookingsAction()
}
