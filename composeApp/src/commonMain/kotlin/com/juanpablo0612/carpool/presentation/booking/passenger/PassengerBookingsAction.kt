package com.juanpablo0612.carpool.presentation.booking.passenger

sealed class PassengerBookingsAction {
    data object OnBackClick : PassengerBookingsAction()
    data class OnTabSelected(val tab: PassengerBookingsTab) : PassengerBookingsAction()
    data class OnCancelBookingClick(val bookingId: String) : PassengerBookingsAction()
    data class OnConfirmCancel(val bookingId: String) : PassengerBookingsAction()
    data object OnDismissCancelDialog : PassengerBookingsAction()
    data object OnDismissError : PassengerBookingsAction()
    data object OnDismissSuccess : PassengerBookingsAction()
    data class OnTrackTrip(val tripId: String) : PassengerBookingsAction()
    data class OnRateBooking(
        val bookingId: String,
        val tripId: String,
        val rateeId: String,
        val rateeName: String
    ) : PassengerBookingsAction()
}
